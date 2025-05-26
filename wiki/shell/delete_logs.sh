#!/bin/bash

LOG_DIR="./logs"

# Проверка, существует ли каталог
if [ -d "$LOG_DIR" ]; then
  echo "Удаление всех файлов в каталоге $LOG_DIR..."
  rm -f "$LOG_DIR"/*
  echo "Готово."
else
  echo "Каталог $LOG_DIR не существует."
fi
