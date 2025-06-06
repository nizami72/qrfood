#!/bin/bash

# Настройки подключения

DB_NAME="qrfood"
DB_USER="intellij"
DB_PASS="intellijpass"

# Подтверждение (необязательно — можно убрать для автоудаления)
# read -p "Вы уверены, что хотите удалить базу данных '$DB_NAME'? [y/N]: " confirm
#if [[ "$confirm" != "y" && "$confirm" != "Y" ]]; then
#  echo "Операция отменена."
#  exit 1
#fi

# Удаление базы данных
mysql -u"$DB_USER" -p"$DB_PASS" -e "DROP DATABASE IF EXISTS \`$DB_NAME\`;"
if [ $? -eq 0 ]; then
  echo "Database '$DB_NAME' dropped."
else
  echo "Errore while database deleting '$DB_NAME'."
fi

mysql -u"$DB_USER" -p"$DB_PASS" -e "CREATE DATABASE \`$DB_NAME\`;"

  echo "Empty database '$DB_NAME' created."
