void debug() {
  fill(150);
  rect(x - 250, y - 100, 250, 100, 10, 10, 0, 10);
  fill(0);
  textSize(15);
  text(mouseX + "\n" + mouseY, x-240, y-80);
  text(scrollVal, x-240, y-40);
}

