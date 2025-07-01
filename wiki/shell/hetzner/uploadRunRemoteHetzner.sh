#!/bin/bash
cd /home/nizami/Dropbox/projects/Java/qrfood
ip=157.180.16.28

echo "Working dir:"$(pwd)

# --- Build FE ---
echo "Building frontend for production..."
cd frontend
npm run build
cd ..

# local clean and build jar file
echo "Clean last build ..."
mvn clean package -DskipTests

echo "Building jar ..."

# upload environment file
echo "Upload environment file ..."
scp -i /home/nizami/.ssh/key2 /home/nizami/Dropbox/projects/Java/qrfood/wiki/shell/hetzner/envHetzner.conf root@$ip:/home/qrfood/env.conf

# upload jar file to EC2
echo "Upload jar file ..."
scp -i /home/nizami/.ssh/key2  target/qrfood.jar  root@$ip:/opt/apps/

# --- upload frontend files ---
FRONTEND_LOCAL_PATH="/home/nizami/Dropbox/projects/Java/qrfood/frontend/dist"
FRONTEND_REMOTE_PATH="/var/www/qrfood.az/dist"

echo "Uploading frontend files from $FRONTEND_LOCAL_PATH to root@$ip:$FRONTEND_REMOTE_PATH ..."
scp -i /home/nizami/.ssh/key2 -r $FRONTEND_LOCAL_PATH/* root@$ip:$FRONTEND_REMOTE_PATH/

# restart service
echo "Restarting service ... "
ssh -i /home/nizami/.ssh/key2 root@$ip 'sudo systemctl restart qrfood.service'

echo "Sleeping for application rebooting ..."
sleep 20s

# get app output
wget -qO- https://qrfood.az/alive

echo -e "\nFinished"
