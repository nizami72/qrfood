#!/bin/bash
cd /home/nizami/Dropbox/projects/Java/qrfood
# $0 is the script name, $1 id the first ARG, $2 is second...
ip=157.180.16.28

# Function to check if variables are defined
check_variables() {
    for var in "$@"; do
        if [ -z "${!var}" ]; then
            echo "Variable '$var' is not defined."
            return 1
        fi
    done
    return 0
}

# Check if variables are defined
check_variables ip

# If variables are not defined, wait for user input
while [ $? -ne 0 ]; do
    read -p "Please enter the remote server ip: " ip
    check_variables ip
done

echo "Working dir:"$(pwd)

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

# restart service
echo "Restarting service ... "
ssh -i /home/nizami/.ssh/key2 root@$ip 'sudo systemctl restart qrfood.service'

echo "Sleeping for application rebooting ..."
sleep 20s

# get app output
wget -qO- https://qrfood.az/alive

echo -e "\nFinished"