#!/bin/sh 

tmux new-session -s "thesis" -d

tmux send-keys -t "thesis:0.0" 'htop; exit' Enter
tmux split-window -v 'watch docker ps'
tmux split-window -v "watch kubectl get events -A  --sort-by='.metadata.creationTimestamp'"
tmux split-window -v 'watch kubectl get nodes'
tmux split-window -h 'watch kubectl get po -A'

tmux resize-pane -t "thesis:0.0" -y 20
tmux resize-pane -t "thesis:0.3" -y 15

tmux -2 attach-session -d

