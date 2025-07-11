#!/bin/bash

# uploading predefined json
APP_ROOT_FOLDER_LOCAL="/home/nizami/projects_resources/qr_food"
APP_ROOT_FOLDER_REMOTE="/home/qrfood"
PRDEF_JSON_FILE="CommonCategories.json"

echo "Uploading predefined json file $APP_ROOT_FOLDER_LOCAL/$PRDEF_JSON_FILE to root@$ip:$APP_ROOT_FOLDER_REMOTE ..."
scp -i /home/nizami/.ssh/key2 "$APP_ROOT_FOLDER_LOCAL/$PRDEF_JSON_FILE" root@$ip:"$APP_ROOT_FOLDER_REMOTE/"
