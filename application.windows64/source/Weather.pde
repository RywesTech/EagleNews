String description;
float temp_;
int temp;

void getWeather() {

  final JSONObject json;
  final JSONArray weather;
  final JSONObject finalWeather;
  final JSONObject main;

  String weatherIcon;

  json = loadJSONObject("http://www.openweathermap.org/data/2.5/weather?q=portland,us&cnt=1&mode=json&units=metric");
  println(json);

  weather = json.getJSONArray("weather");
  finalWeather = weather.getJSONObject(0);
  weatherIcon = finalWeather.getString("icon");
  description = finalWeather.getString("description");
  println("done loading weather, it took " + millis() / 1000.0 + " seconds");

  main = json.getJSONObject("main");
  temp_ = main.getFloat("temp");
  temp = int(temp_ / 5 * 9 + 32);

  icon = loadImage("icns/" + weatherIcon + ".png");
  println("done loading image, it took " + millis() / 1000.0 + " seconds");
  loaded = true;
}

