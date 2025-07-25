#!/bin/bash
# This script automates the synchronization of translation files.

# Navigate to the directory containing the translation files
cd /home/nizami/Dropbox/projects/Java/qrfood/frontend/src/i18n/translations

# Execute the Gemini CLI with the command to analyze and update the files
gemini --prompt "analyze az.js, en.js and ru.js files compare them to each other find out where it has missing key/value translation, add installation if missing, do not change anything else"
