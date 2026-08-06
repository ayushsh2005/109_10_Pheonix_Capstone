#!/usr/bin/env bash
# One-time setup for a fresh Ubuntu/Debian VM.
# Usage: ./bootstrap-vm.sh <github-owner>/<repo> [deploy-dir]
set -euo pipefail

REPO="${1:?Usage: bootstrap-vm.sh <github-owner>/<repo> [deploy-dir]}"
DEPLOY_DIR="${2:-/opt/portfolio-manager}"

echo "==> Installing Docker Engine + Compose plugin"
if ! command -v docker >/dev/null 2>&1; then
  curl -fsSL https://get.docker.com | sh
fi
sudo usermod -aG docker "$USER"

echo "==> Creating deploy directory at $DEPLOY_DIR"
sudo mkdir -p "$DEPLOY_DIR"
sudo chown "$USER":"$USER" "$DEPLOY_DIR"
cd "$DEPLOY_DIR"

echo "==> Fetching docker-compose.yml + .env.example from $REPO"
curl -fsSL -o docker-compose.yml "https://raw.githubusercontent.com/${REPO}/main/docker-compose.yml"
curl -fsSL -o .env.example      "https://raw.githubusercontent.com/${REPO}/main/.env.example"

if [ ! -f .env ]; then
  cp .env.example .env
  echo "!! Edit $DEPLOY_DIR/.env with real values (DB_PASSWORD, image names, etc.) before starting services."
fi

cat <<EOF

==> Bootstrap complete.

Next steps:
  1. Log out and back in (or run 'newgrp docker') so the docker group applies.
  2. Edit $DEPLOY_DIR/.env with real secrets.
  3. Fill in these GitHub Actions repo secrets so CD can reach this box:
       VM_HOST          = $(curl -fsSL ifconfig.me 2>/dev/null || echo "<this VM's IP/hostname>")
       VM_USER          = $USER
       VM_PORT          = 22
       VM_SSH_KEY       = <private key matching a public key in ~/.ssh/authorized_keys>
       VM_DEPLOY_PATH   = $DEPLOY_DIR
       GHCR_PULL_TOKEN  = <GitHub PAT with read:packages scope>
  4. First manual start:
       cd $DEPLOY_DIR && docker compose pull && docker compose up -d
EOF
