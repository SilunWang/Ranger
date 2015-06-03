var square = new Rect(0, 0, 100, 100);

square.addTo(stage);
square.fill('red');
square.animate('1.5s', {
  rotation: Math.PI,
  x: 700,
  fillColor: 'green'
});