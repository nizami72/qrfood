#!/bin/bash


# ====================================== Preparation
cd ~/Library/CloudStorage/Dropbox/projects/Java/qrfood
ip=157.180.16.28
echo "Working dir:"$(pwd)


# ====================================== Build FE
echo "Building frontend for production..."
cd frontend
npm run build
cd ..

# ====================================== locally clean and build jar file
echo "Clean last build ..."
mvn clean package -DskipTests
echo "Building jar ..."


# ====================================== upload environment file
echo "Upload environment file ..."
scp -i ~/.ssh/key2 ~/Library/CloudStorage/Dropbox/projects/Java/qrfood/wiki/shell/hetzner/envHetzner.conf root@$ip:/home/qrfood/env.conf


# ====================================== upload jar file
echo "Upload jar file ..."
scp -i ~/.ssh/key2  target/qrfood.jar  root@$ip:/opt/apps/


# ====================================== upload frontend files
FRONTEND_LOCAL_PATH="~/Library/CloudStorage/Dropbox/projects/Java/qrfood/frontend/dist"
FRONTEND_REMOTE_PATH="/var/www/qrfood.az/dist"
echo "Uploading frontend files from $FRONTEND_LOCAL_PATH to root@$ip:$FRONTEND_REMOTE_PATH ..."
scp -i ~/.ssh/key2 -r $FRONTEND_LOCAL_PATH/* root@$ip:$FRONTEND_REMOTE_PATH/


# ====================================== restart service
echo "Restarting service ... "
ssh -i ~/.ssh/key2 root@$ip 'sudo systemctl restart qrfood.service'
echo "Sleeping for application rebooting ..."
sleep 20s

# ====================================== get app output
wget -qO- https://qrfood.az/api/alive
echo -e "\nFinished"
