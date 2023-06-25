curl -X POST \
  -H "X-session: marco" \
  -H "X-session-request-id: $(uuidgen)" \
  --data-binary "@demo_wav.txt" \
  $1
