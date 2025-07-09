#!/bin/bash

ip=157.180.16.28
scp -i /home/nizami/.ssh/key2 /home/nizami/projects_resources/qr_food/predefined-category-images/* root@$ip:/home/qrfood/predefined-category-images
echo "All images have been copied"
