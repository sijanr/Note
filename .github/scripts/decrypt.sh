#!/bin/sh

# Decrypt the file
gpg --quiet --batch --yes --decrypt --passphrase="FIREBASE_SERVICE" \
--output $./app/google-services.json google-services.json.gpg
