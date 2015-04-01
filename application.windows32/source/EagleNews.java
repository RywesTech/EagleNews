import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.*; 
import g4p_controls.*; 
import java.util.Date; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class EagleNews extends PApplet {





ControlP5 cp5;
Textarea[] art_Textarea, id_Textarea;
GCustomSlider art_scroll, id_scroll;

int view = 1, x, y;
int art_cnt;//articles count
int id_cnt;//important dates count
int id_scrollVal;
int extraX;
int t;
int m, day, dayOM, year;
int hour, minute, second;

float scrollVal;
float tickerX;

String ticker;
String art_articles, id_articles;
String title_;
String[] art_title, id_title;
String month, dayOW, date, time;

PFont font;

PImage logo;
PImage icon;

JSONObject text;
JSONObject art_article, id_article;
JSONArray art_list, id_list;
JSONArray important_dates;

boolean debug = false;
boolean loaded = false;
public boolean sketchFullScreen() {
  return true;
}

public void setup() {
  x = displayWidth;
  y = displayHeight;
  //x = 1024;
  //y = 500;
  size(x, y);//This must come first but it needs to be after the x and y are defined

  //thread("loadJSON");
  loadJSON();
  thread("getWeather");

  font = createFont("texgyretermes-regular.otf", 32);
  logo = loadImage("eagleNews.png");

  dayOM = day();
  year = year();

  m = month();
  if (m == 1) {
    month = "January";
  } else if (m == 2) {
    month = "February";
  } else if (m == 3) {
    month = "March";
  } else if (m == 4) {
    month = "April";
  } else if (m == 5) {
    month = "May";
  } else if (m == 6) {
    month = "June";
  } else if (m == 7) {
    month = "July";
  } else if (m == 8) {
    month = "August";
  } else if (m == 9) {
    month = "September";
  } else if (m == 10) {
    month = "October";
  } else if (m == 11) {
    month = "November";
  } else if (m == 12) {
    month = "December";
  }

  day = new Date().getDay();
  if (day == 1) {
    dayOW = "Monday";
  } else if (day == 2) {
    dayOW = "Tuesday";
  } else if (day == 3) {
    dayOW = "Wednesday";
  } else if (day == 4) {
    dayOW = "Thursday";
  } else if (day == 5) {
    dayOW = "Friday";
  } else if (day == 6) {
    dayOW = "Saturday";
  } else if (day == 0) {
    dayOW = "Sunday";
  }

  println("===================================================");
  println(month);
  println(dayOW);
  println(dayOM);
  println(year);
  println("");
  println(dayOW + ", " + month + " " + dayOM + ", " + year);
  date = dayOW + ", " + month + " " + dayOM + ", " + year;
  println("===================================================");

  textSize(25);
  tickerX = textWidth(date) + 40;

  cp5 = new ControlP5(this);

  art_cnt = text.getInt("art_cnt");
  id_cnt = text.getInt("id_cnt");
  ticker = text.getString("ticker");
  art_list = text.getJSONArray("articles");
  id_list = text.getJSONArray("important_dates");
  art_Textarea = new Textarea[art_cnt];
  id_Textarea = new Textarea[id_cnt];

  art_title = new String[art_cnt];
  id_title = new String[id_cnt];

  for (int i = 0; i < art_cnt; i++) {
    art_Textarea[i] = cp5.addTextarea("txt" + i)
      .setPosition(i*320+10, 130)
        .setSize(300, y-390)
          .setFont(createFont("texgyretermes-regular.otf", 18))
            .setLineHeight(18)
              .setColor(color(0))
                .setColorBackground(color(100, 150, 255))
                  .setColorForeground(color(150))
                    .setColorActive(color(200));
    ;

    art_article = art_list.getJSONObject(i);
    art_articles = art_article.getString("text");
    println(art_articles);
    art_Textarea[i].setText(art_articles);

    art_title[i] = art_article.getString("title");
    println(art_title[i]);
  }

  extraX = x - 600;

  for (int i = 0; i < id_cnt; i++) {
    id_Textarea[i] = cp5.addTextarea("date" + i)
      .setPosition(i*200+305, y - 220)
        .setSize(190, 155)
          .setFont(createFont("texgyretermes-regular.otf", 15))
            .setLineHeight(18)
              .setColor(color(0))
                .setColorBackground(color(60, 140, 240))
                  .setColorForeground(color(150))
                    .setColorActive(color(200));
    ;

    id_article = id_list.getJSONObject(i);
    id_articles = id_article.getString("text");
    println(id_articles);
    id_Textarea[i].setText(id_articles);

    id_title[i] = id_article.getString("date");
    println(id_title[i]);
  }

  //text = list.getJSONObject(i);
  art_scroll = new GCustomSlider(this, x / 2 - 150, y-270, 300, 50, null);
  art_scroll.setShowDecor(false, false, false, false);
  art_scroll.setNbrTicks(5);
  if (art_cnt * 320 <= x) {
    art_scroll.setLimits(((art_cnt * 320) - x)/2, (art_cnt * 320) - x, 0);
    art_scroll.setVisible(false);
  } else {
    art_scroll.setLimits(0, 0, (art_cnt * 320) - x);
  }

  id_scroll = new GCustomSlider(this, x / 2 - 150, y-80, 300, 50, null);
  id_scroll.setShowDecor(false, false, false, false);
  id_scroll.setNbrTicks(5);
  if (id_cnt * 200 <= extraX) {
    id_scroll.setLimits(((id_cnt * 200) - extraX)/2, (id_cnt * 200) - extraX, 0);
    id_scroll.setVisible(false);
  } else {
    id_scroll.setLimits(0, 0, (id_cnt * 200) - extraX);
  }

  cp5.setAutoDraw(false);

  art_scroll.setEasing(2);
  id_scroll.setEasing(2);

  println("Setup took " + millis() / 1000.0f + " seconds.");
}

public void draw() {

  hour = hour();
  minute = minute();
  second = second();
  time = hour + ":" + minute + ":" + second;

  try {
    textFont(font);
  }
  catch(Exception e) {
    System.err.println("Error 003. Reason: " + e);
  }

  switch(view) {
  case 0://Main Menu
    background(100, 150, 255);
    hideTB();
    art_scroll.setVisible(false);
    id_scroll.setVisible(false);
    image(logo, x/2-200, 50);

    fill(150);
    if (mouseX >= x/2 - 200 && mouseX <= x/2 + 200 && mouseY >= 300 && mouseY <= 350) {
      fill(200);
      if (mousePressed) {
        view = 1;
      }
    }
    rect(x/2 - 200, 300, 400, 50, 100);

    fill(150);
    if (mouseX >= x/2 - 200 && mouseX <= x/2 + 200 && mouseY >= 360 && mouseY <= 410) {
      fill(200);
      if (mousePressed) {
        view = 2;
      }
    }
    rect(x/2 - 200, 360, 400, 50, 100);

    fill(150);
    if (mouseX >= x/2 - 200 && mouseX <= x/2 + 200 && mouseY >= 420 && mouseY <= 480) {
      fill(200);
      if (mousePressed) {
        exit();
      }
    }
    rect(x/2 - 200, 420, 400, 50, 100);

    fill(0);
    textSize(30);
    textAlign(CENTER);
    text("View Current News", x/2, 335);
    text("Options", x/2, 395);
    text("Quit", x/2, 455);
    textAlign(LEFT);

    break;
  case 1://Main Viewing Case

    background(100, 150, 255);

    showTB();

    if (art_cnt * 320 <= x) {
      art_scroll.setVisible(false);
    } else {
      art_scroll.setVisible(true);
    }

    if (id_cnt * 200 <= extraX) {
      id_scroll.setVisible(false);
    } else {
      id_scroll.setVisible(true);
    }

    fill(150);
    rect(0, 0, x, 50);
    rect(0, y - 50, x, 50);

    fill(60, 140, 240);
    rect(300, y - 240, x - 600, 190);

    for (int i = 0; i < id_cnt + 1; i++) {
      line(i * 200 - id_scrollVal + 300, y - 240, i * 200 - id_scrollVal + 300, y - 50);
    }
    for (int i = 0; i < id_cnt; i++) {
      id_Textarea[i].setPosition((i*200+305)-id_scrollVal, y - 215);
      fill(0);
      textSize(18);
      text(id_title[i], (i*200+305)-id_scrollVal, y-220);
    }
    id_scrollVal = id_scroll.getValueI();

    for (int i = 0; i < art_cnt + 1; i++) {
      line(i * 320 - scrollVal, 50, i * 320 - scrollVal, y - 250);
    }
    for (int i = 0; i < art_cnt; i++) {
      art_Textarea[i].setPosition((i*320+10)-scrollVal, 130);
      textAlign(CENTER, CENTER);
      textSize(25);
      text(art_title[i], (i*320) - scrollVal, 60, 320, 66);
    }
    scrollVal = art_scroll.getValueI();

    cp5.draw();

    fill(70, 150, 250);
    rect(0, y - 240, 300, 190);
    rect(x - 300, y - 240, 300, 190);

    fill(0);
    line(0, y - 250, x, y - 250);

    textAlign(LEFT);
    if (mouseX >= 10 && mouseX <= 110 && mouseY >= 10 && mouseY <= 40) {
      fill(200);
      if (mousePressed) {
        view = 0;
      }
    } else {
      fill(150);
    }
    rect(10, 10, 100, 30, 10);

    if (mouseX >= x - 90 && mouseX <= x - 20 && mouseY >= 10 && mouseY <= 40) {
      fill(200);
      if (mousePressed) {
        exit();
      }
    } else {
      fill(150);
    }
    rect(x - 90, 10, 70, 30, 10);
    fill(0);
    textSize(20);
    text("\u2190 Menu", 18, 33);
    text("Quit", x - 75, 33);

    textSize(25);
    tickerX = tickerX - 4.5f;
    if (tickerX < (textWidth(ticker) * -1) + (textWidth(date) + 20)) {
      tickerX = x - (textWidth(time) + 20);
    }
    text(ticker, tickerX, y-20);

    fill(200);
    rect(0, y - 50, textWidth(date) + 20, 50);
    rect(x - (textWidth(time) + 20), y - 50, textWidth(time) + 20, 50);
    fill(0);
    text(date, 10, y - 20);
    text(time, x - (textWidth(time) + 10), y - 20);

    if (loaded) {
      try {
        image(icon, 0, y - 230, 150, 150);
        textSize(70);
        text(temp + "\u00b0F", 140, y - 135);
        textSize(30);
        textAlign(CENTER);
        text(description, 100, y - 120, 180, 500);
        textAlign(LEFT);
      }
      catch(Exception e) {
        System.err.println("Error 002. Message: " + e);
      }
    } else {
      textSize(70);
      text("--\u00b0F", 140, y - 135);
      textSize(30);
      text("-----------", 140, y - 100);
    }

    break;
  case 2:
    background(100, 150, 255);
    break;
  default:
    System.err.println("ERROR 001");
    break;
  }

  if (debug == true) {
    debug();
  }
}
/*
Wednesday
 
 September
 
 30
 
 2020
 */
public void hideTB() {
  for (int i = 0; i < art_cnt; i++) {
    art_Textarea[i].hide();
  }
}

public void showTB() {
  for (int i = 0; i < art_cnt; i++) {
    art_Textarea[i].show();
  }
}

String description;
float temp_;
int temp;

public void getWeather() {

  final JSONObject json;
  final JSONArray weather;
  final JSONObject finalWeather;
  final JSONObject main;

  String weatherIcon;

  json = loadJSONObject("http://www.openweathermap.org/data/2.5/weather?q=portland,us&cnt=1&mode=json&units=metric");
  //json = null;
  println(json);

  weather = json.getJSONArray("weather");
  finalWeather = weather.getJSONObject(0);
  weatherIcon = finalWeather.getString("icon");
  description = finalWeather.getString("description");
  println("done loading weather, it took " + millis() / 1000.0f + " seconds");

  main = json.getJSONObject("main");
  temp_ = main.getFloat("temp");
  temp = PApplet.parseInt(temp_ / 5 * 9 + 32);

  icon = loadImage("icns/" + weatherIcon + ".png");
  println("done loading image, it took " + millis() / 1000.0f + " seconds");
  loaded = true;
}

public void debug() {
  fill(150);
  rect(x - 250, y - 100, 250, 100, 10, 10, 0, 10);
  fill(0);
  textSize(15);
  text(mouseX + "\n" + mouseY, x-240, y-80);
  text(scrollVal, x-240, y-40);
}

public void loadJSON() {
  //text = loadJSONObject("/Users/ryan/Desktop/latest.eaglenews");
  text = loadJSONObject("Z:\\IDD-SHARE\\Student Council\\EAGLE NEWS\\latest.eaglenews");
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--hide-stop", "EagleNews" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
