#!/bin/bash

# Настройки
DB_NAME="qrfood"
DB_USER="intellij"
DB_PASS="intellijpass"
DUMP_FILE="/home/nizami/Dropbox/projects/Java/qrfood/wiki/db_dumps/qrfood_13-05-2025 16-41-59.sql"
intellijpass
# 1. Создание базы данных
mysql -u"$DB_USER" -p"$DB_PASS" -e "CREATE DATABASE IF NOT EXISTS \`$DB_NAME\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. Восстановление из дампа
mysql -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" < "$DUMP_FILE"

echo "✅ База данных '$DB_NAME' успешно создана и восстановлена из '$DUMP_FILE'"
