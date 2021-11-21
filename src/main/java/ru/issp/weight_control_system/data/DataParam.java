package ru.issp.weight_control_system.data;

public class  DataParam {

    //TODO Проверить размерность всех величин
//все значения указываются в системе си

    private double  ki = 0.001d, kp = 0.001d, kd = 0.001d;
    private double dNPmax = 10d;
    private double dNPmin =-10d;
    private double kPdefp = 1d;
    private double kPdefm = 1d;

//радиус тигля в мм
    private double R_cruc = 48.000d;
//площадь основания формообразователя кв.мм
    private double S_die = 750.000d;
//площадь верхней части формообразователя кв.мм
    private double S_die_cr = 350.000d;
//скорость выращивания
    private double V_upper = 0.100d;
    //скорость выращивания
    private double V_lower = 0.000d;

    public double getV_cr_upper() {
        return V_cr_upper;
    }

    public void setV_cr_upper(double v_cr_upper) {
        V_cr_upper = v_cr_upper;
    }

    private double V_cr_upper = 0d;

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

    public double getKi() {
        return ki;
    }

    public void setKi(double ki) {
        this.ki = ki;
    }

    public double getKp() {
        return kp;
    }

    public void setKp(double kp) {
        this.kp = kp;
    }

    public double getKd() {
        return kd;
    }

    public void setKd(double kd) {
        this.kd = kd;
    }

    public double getdNPmax() {
        return dNPmax;
    }

    public void setdNPmax(double dNPmax) {
        this.dNPmax = dNPmax;
    }

    public double getdNPmin() {
        return dNPmin;
    }

    public void setdNPmin(double dNPmin) {
        this.dNPmin = dNPmin;
    }

    public double getkPdefp() {
        return kPdefp;
    }

    public void setkPdefp(double kPdefp) {
        this.kPdefp = kPdefp;
    }

    public double getkPdefm() {
        return kPdefm;
    }

    public void setkPdefm(double kPdefm) {
        this.kPdefm = kPdefm;
    }
}
