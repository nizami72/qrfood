#!/bin/bash

# === CONFIGURATION ===
DB_HOST="localhost"
DB_USER="intellij"
DB_PASS="intellijpass"
DB_NAME="qrfood"
BACKUP_DIR="../db_dumps"
DATE=$(date +"%d-%m-%Y %H-%M-%S")
DUMP_FILE="${BACKUP_DIR}/${DB_NAME}_${DATE}.sql"

# === CREATE BACKUP DIRECTORY IF IT DOESN'T EXIST ===
mkdir -p "$BACKUP_DIR"

# === DUMP DATABASE ===
mysqldump -h "$DB_HOST" -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" > "$DUMP_FILE"

# === CHECK IF DUMP WAS SUCCESSFUL ===
if [ $? -eq 0 ]; then
    echo "Database '$DB_NAME' dumped successfully to $DUMP_FILE"
else
    echo "Failed to dump database '$DB_NAME'" >&2
    exit 1
fi
