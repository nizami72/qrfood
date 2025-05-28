#!/bin/bash

# Use TARGET_DIR environment variable
if [ -z "$TARGET_DIR" ]; then
  echo "Error: TARGET_DIR environment variable is not set."
  echo "Usage: TARGET_DIR=/path/to/dir ./clean_directory.sh"
  exit 1
fi

# Confirm the directory exists
if [ ! -d "$TARGET_DIR" ]; then
  echo "Error: Directory '$TARGET_DIR' does not exist."
  exit 1
fi

# Remove all contents (including hidden files, excluding the directory itself)
echo "Cleaning directory: $TARGET_DIR"
rm -rf "$TARGET_DIR"/*

echo "Directory cleaned: $TARGET_DIR"
