package ru.issp.weight_control_system.data;

public class DataParam {

    //FIXME поискать паттерн для констант и переменных
    // - https://www.baeldung.com/java-constants-good-practices
    // - https://stackoverflow.com/questions/66066/what-is-the-best-way-to-implement-constants-in-java
    //TODO Добавить возможность установки параметров через интерфейс
//все значения куазываются в системе си


    Double znam;
Double part4;
Double part5;

public Double firstDerivativeM(double V_upper){
    znam = Math.pow(R_cruc,2)-S_die/pi;
    part4 = rol*Math.pow(r,2)*(pi*Math.pow(R_cruc,2)-(S_die-S_die_cr))*V_upper;
    part5 = pi*rol*S_die_cr*Math.pow(R_cruc,2)*V_lower;
    return (part4-part5)/znam;
}
//радиус тигля в мм
    private double R_cruc = 48.000d;
//площадь основания формообразователя кв.мм
    private double S_die = 750.000d;
//площадь верхней части формообразователя кв.мм
    private double S_die_cr = 350.000d;
//скорость выращивания
    private double V_upper = 1.000d;
    //скорость выращивания
    private double V_lower = 0.000d;
//Пи
    private double pi = Math.PI;
//плотность вещества в твердом состоянии
    private double ros = 3.980d;
//плотность вещества в жидком состоянии
    private double rol = 3.000d;
//радиус кристалла
    private double r = 10.000;


    public double getR_cruc() {
        return R_cruc;
    }

    public void setR_cruc(double r_cruc) {
        R_cruc = r_cruc;
    }

    public double getS_die() {
        return S_die;
    }

    public void setS_die(double s_die) {
        S_die = s_die;
    }

    public double getS_die_cr() {
        return S_die_cr;
    }

    public void setS_die_cr(double s_die_cr) {
        S_die_cr = s_die_cr;
    }

    public double getV_upper() {
        return V_upper;
    }

    public void setV_upper(double v_upper) {
        V_upper = v_upper;
    }

    public double getV_lower() {
        return V_lower;
    }

    public void setV_lower(double v_lower) {
        V_lower = v_lower;
    }

    public double getPi() {
        return pi;
    }

    public void setPi(double pi) {
        this.pi = pi;
    }

    public double getRos() {
        return ros;
    }

    public void setRos(double ros) {
        this.ros = ros;
    }

    public double getRol() {
        return rol;
    }

    public void setRol(double rol) {
        this.rol = rol;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }
}
