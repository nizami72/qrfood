#!/bin/bash

ip=157.180.16.28
scp -i /home/nizami/.ssh/key2 -r /home/nizami/projects_resources/qr_food/images/predefined root@$ip:/home/qrfood/
echo "All images have been copied"
