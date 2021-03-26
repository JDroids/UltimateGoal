package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.ValueProvider;
import com.acmerobotics.dashboard.config.variable.BasicVariable;
import com.acmerobotics.dashboard.config.variable.CustomVariable;
import com.qualcomm.hardware.lynx.LynxDcMotorController;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImpl;
import com.qualcomm.robotcore.hardware.HardwareDevice;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.util.List;
import java.util.Set;

public class DeviceTester extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        List<LynxModule> lynxModules = hardwareMap.getAll(LynxModule.class);
        for (LynxModule lynxModule : lynxModules) {
            try {
                setUpConfigurationVariables(
                        lynxModule,
                        hardwareMap.getNamesOf(lynxModule).iterator().next());
            }
            catch (RobotCoreException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to set up configuration variables");
            }
        }

        //noinspection StatementWithEmptyBody
        while (opModeIsActive()) ; // noop
    }

    private void setUpConfigurationVariables(LynxModule lynxModule, String hubName)
            throws InterruptedException, RobotCoreException {
        CustomVariable hubVar = new CustomVariable();
        FtcDashboard.getInstance().getConfigRoot().putVariable(hubName, hubVar);

        CustomVariable motorVar = new CustomVariable();
        LynxDcMotorController motorController = new LynxDcMotorController(AppUtil.getDefContext(), lynxModule);

        for (int port = 0; port < 4; port++) {
            DcMotor motor = new DcMotorImpl(motorController, port);
            
            motorVar.putVariable(getDeviceName(port, motor), new BasicVariable<>(new ValueProvider<Double>() {
                @Override
                public Double get() {
                    return motor.getPower();
                }

                @Override
                public void set(Double value) {
                    motor.setPower(value);
                }
            }));
        }
        hubVar.putVariable("Motors", motorVar);
        FtcDashboard.getInstance().updateConfig();
    }

    private String getDeviceName(int port, HardwareDevice device) {
        String deviceName = String.valueOf(port);
        Set<String> deviceNameSet = hardwareMap.getNamesOf(device);

        if (!deviceNameSet.isEmpty()) {
            deviceName += ": " + deviceNameSet.iterator().next();
        }

        return deviceName;
    }
}
