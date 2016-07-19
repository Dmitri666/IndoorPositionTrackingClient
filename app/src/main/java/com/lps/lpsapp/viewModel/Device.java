package com.lps.lpsapp.viewModel;

/**
 * Created by dle on 20.08.2015.
 */
public class Device {

    public String deviceId;
    /// <summary>
    ///     Gets or sets the build number.
    /// </summary>
    public String buildNumber;
    /// <summary>
    ///     Gets or sets the manufacturer.
    /// </summary>
    public String manufacturer;
    /// <summary>
    ///     Gets or sets the model.
    /// </summary>
    public String model;
    /// <summary>
    ///     Gets or sets the version.
    /// </summary>
    public String version;

    public Device() {

    }

    public Device(String deviceId, String buildNumber, String manufacturer, String model, String version) {
        this.buildNumber = buildNumber;
        this.manufacturer = manufacturer;
        this.model = model;
        this.version = version;
        this.deviceId = deviceId;
    }
}
