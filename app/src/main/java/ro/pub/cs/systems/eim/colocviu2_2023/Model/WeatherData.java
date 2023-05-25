package ro.pub.cs.systems.eim.colocviu2_2023.Model;

public class WeatherData {
    private String temperature;
    private String wind;
    private String status;
    private String pressure;
    private String humidity;

    public WeatherData(String temperature, String wind, String status, String pressure, String humidity) {
        this.temperature = temperature;
        this.wind = wind;
        this.status = status;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "temperature='" + temperature + '\'' +
                ", wind='" + wind + '\'' +
                ", status='" + status + '\'' +
                ", pressure='" + pressure + '\'' +
                ", humidity='" + humidity + '\'' +
                '}';
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
}
