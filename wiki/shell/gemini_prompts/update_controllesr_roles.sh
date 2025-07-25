#!/bin/bash
exec &>> /var/log/sync_translations.log # Redirects stdout and stderr to a log file

# Navigate to the directory containing the file
cd /home/nizami/Dropbox/projects/Java/qrfood

echo "$(date): Starting process." >> /var/log/sync_translations.log

# Execute the Gemini CLI with the command to analyze and update the files
gemini --prompt "
analyze CONTROLLER_ROLE.md file,
delete 'Suggested Role' column,
update Current Role column with the role who is eligible to execute corresponding method as per app logic,
do not mention SUPER_ADMIN role at all, only all remaining roles except SUPER_ADMIN,
add one more column and write there the url path corresponding to the controller method,
before editing the file create the copy of one"

echo "$(date): Translation sync finished." >> /var/log/sync_translations.log
