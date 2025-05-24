#!/bin/bash

FOLDER="logs"

echo "Try to delete files in $FOLDER."
# Delete all files (not folders) in the specified folder
find "$FOLDER" -type f -exec rm -f {} \;

echo "All files in $FOLDER have been deleted."
