import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.json.JSONObject;

public class WeatherApp {
    // OpenWeatherMap API密钥（需要替换为你自己的API key）
    private static final String API_KEY = "your_api_key_here";
    // API基础URL
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";

    public static void main(String[] args) {
        System.out.println("欢迎使用天气查询程序!");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("请输入要查询的城市名称 (输入'q'退出): ");
            String city = scanner.nextLine().trim();

            if ("q".equalsIgnoreCase(city)) {
                System.out.println("感谢使用，再见!");
                break;
            }

            if (city.isEmpty()) {
                System.out.println("错误: 城市名称不能为空");
                continue;
            }

            // 获取并显示天气信息
            try {
                String weatherData = fetchWeatherData(city);
                System.out.println(formatWeatherData(weatherData));
            } catch (Exception e) {
                System.out.println("获取天气数据时出错: " + e.getMessage());
            }
        }

        scanner.close();
    }

    /**
     * 从OpenWeatherMap API获取天气数据
     * 
     * @param city 城市名称
     * @return JSON格式的天气数据字符串
     * @throws IOException 如果网络请求出错
     */
    private static String fetchWeatherData(String city) throws IOException {
        // 构建API请求URL
        String url = String.format("%s?q=%s&appid=%s&units=metric", 
                BASE_URL, city, API_KEY);
        
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        // 设置请求方法
        con.setRequestMethod("GET");
        
        // 读取响应
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        
        return response.toString();
    }

    /**
     * 格式化天气数据以便于显示
     * 
     * @param jsonData JSON格式的天气数据
     * @return 格式化后的天气信息字符串
     */
    private static String formatWeatherData(String jsonData) {
        if (jsonData == null || jsonData.isEmpty()) {
            return "无法获取天气数据";
        }
        
        JSONObject weatherJson = new JSONObject(jsonData);
        
        // 提取关键信息
        String city = weatherJson.getString("name");
        String country = weatherJson.getJSONObject("sys").getString("country");
        String weatherDesc = weatherJson.getJSONArray("weather")
                .getJSONObject(0).getString("description");
        double temp = weatherJson.getJSONObject("main").getDouble("temp");
        double feelsLike = weatherJson.getJSONObject("main").getDouble("feels_like");
        int humidity = weatherJson.getJSONObject("main").getInt("humidity");
        double windSpeed = weatherJson.getJSONObject("wind").getDouble("speed");
        
        // 获取日出日落时间（转换为本地时间）
        long sunriseTimestamp = weatherJson.getJSONObject("sys").getLong("sunrise") * 1000;
        long sunsetTimestamp = weatherJson.getJSONObject("sys").getLong("sunset") * 1000;
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String sunriseTime = timeFormat.format(new Date(sunriseTimestamp));
        String sunsetTime = timeFormat.format(new Date(sunsetTimestamp));
        
        // 构建格式化的输出
        return String.format("""
===== %s, %s 的天气信息 =====
当前天气状况: %s
当前温度: %.1f°C
体感温度: %.1f°C
湿度: %d%%
风速: %.1f m/s
日出时间: %s
日落时间: %s
=======================================
""", city, country, weatherDesc, temp, feelsLike, humidity, windSpeed, sunriseTime, sunsetTime);
    }
}
