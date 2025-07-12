import requests
import json
from datetime import datetime

# OpenWeatherMap API密钥（需要替换为你自己的API key）
API_KEY = "your_api_key_here"
# API基础URL
BASE_URL = "http://api.openweathermap.org/data/2.5/weather"

def get_weather(city_name):
    """
    获取指定城市的天气信息
    
    参数:
    city_name (str): 城市名称
    
    返回:
    dict: 包含天气信息的字典，如果出错则返回None
    """
    try:
        # 构建API请求参数
        params = {
            'q': city_name,
            'appid': API_KEY,
            'units': 'metric'  # 使用公制单位（摄氏度）
        }
        
        # 发送HTTP GET请求
        response = requests.get(BASE_URL, params=params)
        
        # 检查响应状态码
        if response.status_code == 200:
            # 请求成功，解析JSON数据
            weather_data = response.json()
            return weather_data
        else:
            # 请求失败，打印错误信息
            print(f"Error: API请求失败，状态码 {response.status_code}")
            print(f"Error details: {response.text}")
            return None
    
    except requests.exceptions.RequestException as e:
        # 处理网络异常
        print(f"Error: 网络请求异常 - {e}")
        return None

def format_weather_data(weather_data):
    """
    格式化天气数据，以便于打印输出
    
    参数:
    weather_data (dict): 包含天气信息的字典
    
    返回:
    str: 格式化后的天气信息字符串
    """
    if not weather_data:
        return "无法获取天气数据"
    
    # 提取关键信息
    city = weather_data['name']
    country = weather_data['sys']['country']
    weather_desc = weather_data['weather'][0]['description']
    temp = weather_data['main']['temp']
    feels_like = weather_data['main']['feels_like']
    humidity = weather_data['main']['humidity']
    wind_speed = weather_data['wind']['speed']
    
    # 获取日出日落时间（转换为本地时间）
    sunrise_timestamp = datetime.fromtimestamp(weather_data['sys']['sunrise'])
    sunset_timestamp = datetime.fromtimestamp(weather_data['sys']['sunset'])
    
    # 构建格式化的输出
    formatted_data = f"""
===== {city}, {country} 的天气信息 =====
当前天气状况: {weather_desc}
当前温度: {temp}°C
体感温度: {feels_like}°C
湿度: {humidity}%
风速: {wind_speed} m/s
日出时间: {sunrise_timestamp.strftime('%H:%M:%S')}
日落时间: {sunset_timestamp.strftime('%H:%M:%S')}
=======================================
"""
    return formatted_data

def main():
    """程序主函数"""
    print("欢迎使用天气查询程序!")
    
    while True:
        city = input("请输入要查询的城市名称 (输入'q'退出): ").strip()
        
        if city.lower() == 'q':
            print("感谢使用，再见!")
            break
        
        if not city:
            print("错误: 城市名称不能为空")
            continue
        
        # 获取并显示天气信息
        weather_data = get_weather(city)
        print(format_weather_data(weather_data))

if __name__ == "__main__":
    main()
