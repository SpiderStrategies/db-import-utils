workflow "Test workflow" {
  on = "push"
  resolves = ["Test"]
}

action "Test" {
  uses = "./action-test"
}
