package ru.issp.weight_control_system.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class DataParamProperties {

    //TODO Проверить размерность всех величин
//все значения указываются в системе си
    public DataParamProperties() {

        //TODO переделать в соответствии с условиями сборки(обработка пути файла)
        // и закрывать поток (try with recourses)

        Properties appProps = new Properties();
/*        try (FileInputStream fileInputStream = new FileInputStream("src/main/resources/app.properties")) {
            appProps.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("app.properties");
        try {
            appProps.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.ki = Double.parseDouble(appProps.getProperty("ki"));
        this.kp = Double.parseDouble(appProps.getProperty("kp"));
        this.kd = Double.parseDouble(appProps.getProperty("kd"));
        this.dNPmax = Double.parseDouble(appProps.getProperty("dNPmax"));
        this.kPdefp = Double.parseDouble(appProps.getProperty("kPdefp"));
        this.kPdefm = Double.parseDouble(appProps.getProperty("kPdefm"));
        this.R_cruc = Double.parseDouble(appProps.getProperty("R_cruc"));
        this.r = Double.parseDouble(appProps.getProperty("r"));
        this.S_die = Double.parseDouble(appProps.getProperty("S_die"));
        this.S_die_cr = Double.parseDouble(appProps.getProperty("S_die_cr"));
        this.V_upper = Double.parseDouble(appProps.getProperty("V_upper"));
        this.V_lower = Double.parseDouble(appProps.getProperty("V_lower"));
        this.V_cr_upper = Double.parseDouble(appProps.getProperty("V_cr_upper"));
        this.ros = Double.parseDouble(appProps.getProperty("ros"));
        this.rol = Double.parseDouble(appProps.getProperty("rol"));
        this.manualRangingSpan = Integer.parseInt(appProps.getProperty("manualRangingSpan"));
        this.modelTact = Integer.parseInt(appProps.getProperty("modelTact"));
        this.readTact = Integer.parseInt(appProps.getProperty("readTact"));
        this.lowPassFilterPeriod=Double.parseDouble(appProps.getProperty("lowPassFilterPeriod"));
    }

    private double  ki;
    private double kp;
    private double kd;
    private double dNPmax;
    private double kPdefp;
    private double kPdefm;
    private double R_cruc;
    private double S_die;
    private double S_die_cr;
    private double V_upper;
    private double V_lower;
    private double V_cr_upper;
    private double ros;
    private double rol;
    private double r;
    private double lowPassFilterPeriod;
    private int manualRangingSpan,modelTact,readTact;


    public double getLowPassFilterPeriod() {
        return lowPassFilterPeriod;
    }

    public void setLowPassFilterPeriod(double lowPassFilterPeriod) {
        this.lowPassFilterPeriod = lowPassFilterPeriod;
    }



    public double getV_cr_upper() {
        return V_cr_upper;
    }

    public void setV_cr_upper(double v_cr_upper) {
        V_cr_upper = v_cr_upper;
    }

    public int getManualRangingSpan() {
        return manualRangingSpan;
    }

    public void setManualRangingSpan(int manualRangingSpan) {
        this.manualRangingSpan = manualRangingSpan;
    }

    public int getModelTact() {
        return modelTact;
    }

    public void setModelTact(int modelTact) {
        this.modelTact = modelTact;
    }

    public int getReadTact() {
        return readTact;
    }

    public void setReadTact(int readTact) {
        this.readTact = readTact;
    }

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

    @Override
    public String toString() {
        return "DataParamProperties{" +
                "ki=" + ki +
                ", kp=" + kp +
                ", kd=" + kd +
                ", dNPmax=" + dNPmax +
                ", kPdefp=" + kPdefp +
                ", kPdefm=" + kPdefm +
                ", R_cruc=" + R_cruc +
                ", S_die=" + S_die +
                ", S_die_cr=" + S_die_cr +
                ", V_upper=" + V_upper +
                ", V_lower=" + V_lower +
                ", V_cr_upper=" + V_cr_upper +
                ", ros=" + ros +
                ", rol=" + rol +
                ", r=" + r +
                ", manualRangingSpan=" + manualRangingSpan +
                ", modelTact=" + modelTact +
                ", readTact=" + readTact +
                '}';
    }
}
