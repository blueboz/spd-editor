package cn.boz.jb.plugin.floweditor.gui.widget;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 导出为图片的按钮
 */
public class ExportImageDialog extends JDialog {

    private double initScale;
    private double w;
    private double h;
    private JLabel scaleLabel;
    private JLabel widthLabel;
    private JLabel heightLabel;
    private JSpinner scaleSpinner;
    private JTextField widthText;
    private JTextField heightText;

    public ExportImageDialog(double initScale, double width, double height) {
        this.setTitle("设置分辨率");
        this.initScale = initScale;
        this.w = width;
        this.h = height;
        this.setModal(true);
        Container contentPane = this.getContentPane();
        GridBagLayout gridBagLayout = new GridBagLayout();
        this.setLayout(gridBagLayout);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 0;
        constraints.insets = new Insets(5, 10, 5, 10);
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.weightx = 0;

        scaleLabel = new JLabel("scale:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.gridwidth = 1;
        gridBagLayout.setConstraints(scaleLabel, constraints);
        contentPane.add(scaleLabel);

        scaleSpinner = new JSpinner();
        SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(initScale, 0.1, 10, 0.01);
        scaleSpinner.setModel(spinnerNumberModel);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.gridwidth = 2;
        gridBagLayout.setConstraints(scaleSpinner, constraints);
        contentPane.add(scaleSpinner);
        ExportImageDialog dlg = this;
        scaleSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Object source = e.getSource();
                if (source instanceof JSpinner) {
                    JSpinner spinner = (JSpinner) source;
                    double value = (double) spinner.getValue();
                    heightText.setText(String.format("%.2f", value * height));
                    widthText.setText(String.format("%.2f", value * width));
                    dlg.setInitScale(value);
                }
            }
        });

        widthLabel = new JLabel("width:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.gridwidth = 1;

        gridBagLayout.setConstraints(widthLabel, constraints);
        contentPane.add(widthLabel);


        widthText = new JTextField();
        widthText.setText(String.format("%.2f", width));
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.gridwidth = 2;
        gridBagLayout.setConstraints(widthText, constraints);
        contentPane.add(widthText);
        widthText.setEnabled(false);


        heightLabel = new JLabel("height:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 0;
        constraints.gridwidth = 1;
        gridBagLayout.setConstraints(heightLabel, constraints);
        contentPane.add(heightLabel);

        heightText = new JTextField();
        heightText.setText(String.format("%.2f", height));
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.gridwidth = 2;
        gridBagLayout.setConstraints(heightText, constraints);
        contentPane.add(heightText);
        heightText.setEnabled(false);


        JButton btn = new JButton();
        btn.setText("确定");
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.gridwidth = 3;
        gridBagLayout.setConstraints(btn, constraints);
        contentPane.add(btn);
        ExportImageDialog exportImageDialog = this;
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                exportImageDialog.setVisible(false);
            }
        });

        int w = 180;
        int h = 180;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle maximumSize = ge.getMaximumWindowBounds();
        this.setBounds(maximumSize.width / 2 - w / 2, maximumSize.height / 2 - h / 2, w, h);
        this.setResizable(false);
    }

    public double getInitScale() {
        return initScale;
    }

    public void setInitScale(double initScale) {
        this.initScale = initScale;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }
}
