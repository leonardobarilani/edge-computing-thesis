watch -n 6 "kubectl config use-context k3d-p3; kubectl get po -A; kubectl config use-context k3d-p2; kubectl get po -A; kubectl config use-context k3d-p1; kubectl get po -A"
