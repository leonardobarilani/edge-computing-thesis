# Check if there are exactly 2 arguments
if [ "$#" -ne 2 ]; then
    echo "Error: URL required as first argument and filename requred as second argument"
    exit 1
fi

curl -X POST \
  -H "X-session: marco" \
  -H "X-session-request-id: $(uuidgen)" \
  --data-binary "@$2" \
  $1
