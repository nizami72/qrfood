#!/bin/bash

# Script to get a Bearer token and delete a user using POST method
# Usage: ./delete_user_post.sh <login_email> <password> <user_name>

# Check if all required arguments are provided
if [ $# -lt 3 ]; then
    echo "Usage: $0 <login_email> <password> <user_name>"
    exit 1
fi

# Assign arguments to variables
SUPER_ADMIN_LOGIN=$1
SUPER_ADMIN_PASSWORD=$2
USER_NAME_TO_DELETE=$3
BASE_URL=$4

# Login and get JWT token
echo "Logging in with email: $SUPER_ADMIN_LOGIN"
JWT_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"email\": \"$SUPER_ADMIN_LOGIN\", \"password\": \"$SUPER_ADMIN_PASSWORD\"}")

# Extract JWT token from response
JWT_TOKEN=$(echo $JWT_RESPONSE | grep -o '"jwt":"[^"]*"' | cut -d'"' -f4)

if [ -z "$JWT_TOKEN" ]; then
    echo "Failed to get JWT token. Response: $JWT_RESPONSE"
    exit 1
fi

echo "Successfully obtained JWT token"

# Delete user using POST method with the JWT token
echo "Deleting user with username: $USER_NAME_TO_DELETE using POST method"
DELETE_RESPONSE=$(curl -s -X POST "$BASE_URL/api/user/$USER_NAME_TO_DELETE" \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -w "%{http_code}")

HTTP_CODE=$(echo "$DELETE_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$DELETE_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 204 ]; then
    echo "User deleted successfully using POST method"
else
    echo "Failed to delete user using POST method. HTTP Code: $HTTP_CODE, Response: $RESPONSE_BODY"
fi

echo "Operation completed successfully"