// Declarative Jenkins pipeline: build & push images, then deploy to the VM
// over SSH. Mirrors .github/workflows/cd.yml - pick ONE of the two in a
// real project; both are included here since the course checklist lists
// both GitHub Actions and Jenkins as options.
//
// Required Jenkins credentials (Manage Jenkins > Credentials):
//   ghcr-creds        (Username/Password) - GitHub username + PAT with
//                      write:packages (build) / read:packages (deploy)
//   vm-ssh-key         (SSH Username with private key) - deploy user on the VM
//
// Required Jenkins parameters / env, set in the job or a .env file loaded
// via the EnvInject plugin:
//   VM_HOST, VM_DEPLOY_PATH

pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        REGISTRY         = 'ghcr.io'
        REPO_OWNER       = 'CHANGE_ME'   // github org/user, lowercase
        BACKEND_IMAGE    = "${REGISTRY}/${REPO_OWNER}/portfolio-backend"
        FRONTEND_IMAGE   = "${REGISTRY}/${REPO_OWNER}/portfolio-frontend"
        IMAGE_TAG        = "${env.GIT_COMMIT.take(12)}"
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Backend: build & test') {
            steps {
                dir('backend') {
                    sh 'chmod +x mvnw && ./mvnw -B -ntp clean verify'
                }
            }
            post {
                always { junit 'backend/target/surefire-reports/*.xml' }
            }
        }

        stage('Frontend: lint & build') {
            steps {
                dir('frontend') {
                    sh 'npm ci'
                    sh 'npm run lint'
                    sh 'VITE_USE_MOCK=true npm run build'
                }
            }
        }

        stage('Docker: build images') {
            steps {
                sh "docker build -t ${BACKEND_IMAGE}:${IMAGE_TAG} -t ${BACKEND_IMAGE}:latest ./backend"
                sh "docker build -t ${FRONTEND_IMAGE}:${IMAGE_TAG} -t ${FRONTEND_IMAGE}:latest ./frontend"
            }
        }

        stage('Docker: push images') {
            when { branch 'main' }
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'ghcr-creds',
                    usernameVariable: 'GHCR_USER',
                    passwordVariable: 'GHCR_TOKEN'
                )]) {
                    sh '''
                        echo "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USER" --password-stdin
                        docker push '"${BACKEND_IMAGE}"':'"${IMAGE_TAG}"'
                        docker push '"${BACKEND_IMAGE}"':latest
                        docker push '"${FRONTEND_IMAGE}"':'"${IMAGE_TAG}"'
                        docker push '"${FRONTEND_IMAGE}"':latest
                        docker logout ghcr.io
                    '''
                }
            }
        }

        stage('Deploy to VM') {
            when { branch 'main' }
            steps {
                sshagent(credentials: ['vm-ssh-key']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=accept-new "$VM_USER@$VM_HOST" '
                            set -euo pipefail
                            cd "'"$VM_DEPLOY_PATH"'"
                            export IMAGE_TAG='"$IMAGE_TAG"'
                            export BACKEND_IMAGE='"$BACKEND_IMAGE"'
                            export FRONTEND_IMAGE='"$FRONTEND_IMAGE"'
                            docker compose pull
                            docker compose up -d --remove-orphans
                            docker image prune -f
                        '
                    '''
                }
            }
        }
    }

    post {
        always { cleanWs() }
    }
}
