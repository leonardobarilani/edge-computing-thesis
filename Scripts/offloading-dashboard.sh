#!/bin/sh

tmux new-session -s "thesis" -d

tmux send-keys -t "thesis:0.0" 'htop; exit' Enter
tmux split-window -v "kubectl get events -A  --sort-by='.metadata.creationTimestamp' -w"
tmux split-window -v 'watch kubectl top pods -A'
tmux split-window -v 'kubectl logs -n openfaas deploy/gateway -f'
tmux split-window -h 'kubectl get po -A -w'

tmux resize-pane -t "thesis:0.0" -y 20
tmux resize-pane -t "thesis:0.3" -y 15

tmux -2 attach-session -d

