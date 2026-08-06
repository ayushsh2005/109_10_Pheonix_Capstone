// Declarative Jenkins pipeline: build, push, and deploy locally on the VM
// where Jenkins itself is running.
//
// Required Jenkins credentials (Manage Jenkins > Credentials):
//   ghcr-creds        (Username/Password) - GitHub username + PAT with
//                      write:packages (build) / read:packages (deploy)
//
// Optional Jenkins environment variable:
//   DEPLOY_DIR        absolute path to the VM directory containing
//                     docker-compose.yml and .env

pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        REGISTRY         = 'ghcr.io'
        REPO_OWNER       = 'neueda-learning'
        BACKEND_IMAGE    = "${REGISTRY}/${REPO_OWNER}/portfolio-backend"
        FRONTEND_IMAGE   = "${REGISTRY}/${REPO_OWNER}/portfolio-frontend"
        IMAGE_TAG        = "${env.GIT_COMMIT.take(12)}"
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Backend build and tests') {
            steps {
                dir('backend') {
                    sh 'chmod +x mvnw && ./mvnw -B -ntp clean verify'
                }
            }
            post {
                always { junit 'backend/target/surefire-reports/*.xml' }
            }
        }

        stage('Frontend build') {
            steps {
                dir('frontend') {
                    sh 'npm ci'
                    sh 'npm run lint'
                    sh 'VITE_USE_MOCK=true npm run build'
                }
            }
        }

        stage('Docker build') {
            steps {
                sh "docker build -t ${BACKEND_IMAGE}:${IMAGE_TAG} -t ${BACKEND_IMAGE}:latest ./backend"
                sh "docker build -t ${FRONTEND_IMAGE}:${IMAGE_TAG} -t ${FRONTEND_IMAGE}:latest ./frontend"
            }
        }

        stage('Docker push') {
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

        stage('Deploy locally') {
            when { branch 'main' }
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'ghcr-creds',
                    usernameVariable: 'GHCR_USER',
                    passwordVariable: 'GHCR_TOKEN'
                )]) {
                    sh '''
                        set -euo pipefail
                        echo "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USER" --password-stdin
                        cd "${DEPLOY_DIR:-$WORKSPACE}"
                        export IMAGE_TAG='"$IMAGE_TAG"'
                        export BACKEND_IMAGE='"$BACKEND_IMAGE"'
                        export FRONTEND_IMAGE='"$FRONTEND_IMAGE"'
                        docker compose pull
                        docker compose up -d --remove-orphans
                        docker image prune -f
                        docker logout ghcr.io
                    '''
                }
            }
        }
    }

    post {
        always { cleanWs() }
    }
}
