package uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.nimrod;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Copyright (C) 2017 geoagdt.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
/**
 *
 * @author geoagdt
 */
public class SARIC_NIMRODHeader {

    /**
     * VT is the Validity Time of the data. For data with a time-period of
     * validity (e.g. precip accumulation over one hour), this is the end of the
     * time-period.
     */
    short VT_year;
    short VT_month;
    short VT_day;
    short VT_hour;
    short VT_minute;
    short VT_second;

    /**
     * DT is the Data Time. It can be used for models, forecast images, or
     * forecast data.
     */
    short DT_year;
    short DT_month;
    short DT_day;
    short DT_hour;
    short DT_minute;

    /**
     * dataType=0 if data is of type real, dataType=1 if data is of type
     * integer, dataType=2 if data is of type byte.
     */
    short dataType;

    /**
     * Number of bytes for each data element (1, 2, or 4).
     */
    short NumberOfBytesForEachDataElement;

    /**
     * Experiment number (user supplied) – must be a multiple of four. 0 for
     * operational output. Number +1 for QV-nowcast and Number+2 for CDP outputs
     */
    short ExperimentNumber;

    /**
     * Horizontal grid type (0=NG, 1=lat/lon, 2=space view, 3=polar
     * stereographic, 4=UTM32 (EuroPP), 5=Rotated Lat Lon, 6=other).
     */
    short HorizontalGridType;

    /**
     * Number of rows in field.
     */
    short nrows;

    /**
     * Number of columns in field.
     */
    short ncols;

    /**
     * Header file release number (2 for the first release of the Nimrod
     * header).
     */
    short HeaderFileReleaseNumber;

    /**
     * Field code number (includes data type).
     */
    short FieldCodeNumber;

    /**
     * Vertical co-ordinate type (0=height above orography, 1=height above
     * sea-level, 2=pressure, 3=sigma, 4=eta, 5=radar beam number,
     * 6=temperature, 7=potential temperature, 8=equivalent potential
     * temperature, 9=wet bulb potential temperature, 10=potential vorticity,
     * 11=cloud boundary, 12=levels below ground).
     */
    short VerticalCoordinateType;

    /**
     * Vertical co-ordinate of reference level eg. for thickness fields (values
     * as for element 20).
     */
    short VerticalCoordinateOfReferenceLevel;

    /**
     * Number of elements, starting at element 60, which are used for
     * data-specific information eg. calibration information only appropriate to
     * a radar image. (this element previously indicated whether or not a
     * supplied colour table is used).
     */
    short NumberOfElementsOfDataSpecificInformationStartingAtElement60;

    /**
     * Number of elements, starting at element 109, which are used for
     * data-specific information (previously this was the number of categories
     * in colour table).
     */
    short NumberOfElementsOfDataSpecificInformationStartingAtElement109;

    /**
     * Location of origin of data (0=top LH corner, 1=bottom LH corner, 2=top RH
     * corner, 3=bottom RH corner).
     */
    short LocationOfOriginOfData;

    /**
     * Integer missing data value.
     */
    short IntegerMissingDataValue;

    /**
     * Period of interest for accumulation, average or probability (minutes) A
     * value of +32767 indicates that element 159 holds this value in seconds
     * rather than minutes.
     */
    int PeriodOfInterestForAccumulationAverageOrProbabilityInMinutes;

    /**
     * Number of Model Levels available for this parameter
     */
    short NumberOfModelLevelsAvailableForThisParameter;

    /**
     * Projection biaxial ellipsoid [ 0 = Airy 1830 (NG), 1 = International 1924
     * (modified UTM-32), 2 = GRS80 (GUGiK 1992/19) ].
     */
    short ProjectionBiaxialEllipsoid;

    /**
     * Ensemble member ID
     */
    short EnsembleMemberID;

    /**
     * Origin model ID (1: nowcast, 2: radar, 11:UKV, 12:UK4, 13:NAE, 14:Global,
     * 15:MOGREPS-EU, 16:MOGREPS-UK, 17:UK4-extended, 18:4km Italy UM
     */
    short OriginModelID;

    /**
     * Time averaging (LBPROC) Combinations of: 1: warm bias applied 2: cold
     * bias applied 4: smoothed 8: only observations used 16: averaged over
     * multiple surface types 32: scaled to UM resolution (e.g. winds) 128:
     * accumulation or average 256: extrapolation 512: time-lagged 4096: minimum
     * in period 8192: maximum in period
     */
    short LBPROC;

    /**
     * Value of vertical co-ordinate (eg. 500.0 for a 500hPa height field), or
     * radar beam number (8888.0=sea-level, 9999.0=ground level or undefined).
     * If the vertical co-ordinate type (element 20) is set to 3 or 4 then the
     * value is set to model level number. For example, 3.0 for model level
     * three or 2.5 for model level two and a half.
     */
    float ValueOfVerticalCoordinate;

    /**
     * Value of reference vertical co-ordinate (eg. 1000.0 for a 500 - 1000hPa
     * thickness field)
     */
    float ValueOfReferenceVerticalCoordinate;

    /**
     * Northing or latitude or start line of first row of data (metres for NG,
     * degrees for PS grids).
     */
    float NorthingOrLatitudeOrStartLineOfFirstRowOfData;

    /**
     * Interval between rows ie. pixel size. For PS images this will be the
     * resolution in the y-direction at the standard latitude of 60 degrees
     * North (metres or degrees).
     */
    float IntervalBetweenRows;

    /**
     * Easting or longitude or start pixel of first point of first row of data
     * (metres or degrees).
     */
    float EastingOrLongitudeOrStartPixelOfFirstPointOfFirstRowOfData;

    /**
     * Interval between columns ie. pixel size. For polar stereographic images
     * this will be the resolution in the x-direction at the standard latitude
     * of 60 degrees North (metres or degrees).
     */
    float IntervalBetweenColumns;

    /**
     * Real missing data value.
     */
    float RealMissingDataValue;

    /**
     * MKS scaling factor for data (=100.0 for pressure in millibars).
     */
    float MKSScalingFactor;

    /**
     * Data offset value.
     */
    float DataOffsetValue;

    /**
     * X-offset of model data from gridpoints (positive = to East, negative = to
     * West).
     */
    float XOffsetOfModelDataFromGridpoints;

    /**
     * Y-offset of model data from gridpoints (positive = to North, negative =
     * to South)
     */
    float YOffsetOfModelDataFromGridpoints;

    /**
     * Standard latitude or latitude of true origin(TM or PS projection) in
     * degrees
     */
    float StandardLatitudeOrLatitudeOfTrueOrigin;

    /**
     * Standard longitude or longitude of true origin(TM or PS projections) in
     * degrees
     */
    float StandardLongitudeOrLongitudeOfTrueOrigin;

    /**
     * Easting of true origin (TM Projection) in metres
     */
    float EastinOfTrueOrigin;

    /**
     * Northing of true origin (TM Projection) in metres
     */
    float NorthingOfTrueOrigin;

    /**
     * Scale factor on central meridian for TM Projections [ NG = 0.9996012717,
     * modified UTM-32 = 0.9996, GUGiK 1992/19 = 0.9993 ].
     */
    float ScaleFactorOnCentralMeridian;

    /**
     * Threshold value (e.g. for probabilities and percentiles).
     */
    float ThresholdValue;

    /**
     * Northing or latitude of top left corner of the image (metres for NG,
     * degrees for PS grids)
     */
    float NorthingOrLatitudeOfTopLeftCornerOfTheImage;

    /**
     * Easting or longitude of top left corner of the image (metres for NG,
     * degrees for PS grids)
     */
    float EastingOrLongitudeOfTopLeftCornerOfTheImage;

    /**
     * Northing or latitude of top right corner of the image (metres for NG,
     * degrees for PS grids)
     */
    float NorthingOrLatitudeOfTopRightCornerOfTheImage;

    /**
     * Easting or longitude of top right corner of the image (metres for NG,
     * degrees for PS grids)
     */
    float EastingOrLongitudeOfTopRightCornerOfTheImage;

    /**
     * Northing or latitude of bottom right corner of the image (metres for NG,
     * degrees for PS grids)
     */
    float NorthingOrLatitudeOfBottomRightCornerOfTheImage;

    /**
     * Easting or longitude of bottom right corner of the image (metres for NG,
     * degrees for PS grids)
     */
    float EastingOrLongitudeOfBottomRightCornerOfTheImage;

    /**
     * Northing or latitude of bottom left corner of the image (metres for NG,
     * degrees for PS grids)
     */
    float NorthingOrLatitudeOfBottomLeftCornerOfTheImage;

    /**
     * Easting or longitude of bottom left corner of the image (metres for NG,
     * degrees for PS grids)
     */
    float EastingOrLongitudeOfBottomLeftCornerOfTheImage;

    /**
     * Satellite calibration co-efficient
     */
    float SatelliteCalibrationCoefficient;

    /**
     * Space count (satellite data)
     */
    float SpaceCountSatelliteData;

    /**
     * Ducting Index
     */
    float DuctingIndex;

    /**
     * Elevation Angle
     */
    float ElevationAngle;

    /**
     * Neighbourhood size (km) for probabilities
     */
    float NeighbourhoodSizeInKmForProbabilities;

    /**
     * Radius of interest (km) for probabilities
     */
    float RadiusOfInterestInKmForProbabilities;

    /**
     * Recursive filter strength α (for probabilities)
     */
    float RecursiveFilterStrengthForProbabilities;

    /**
     * Fuzzy threshold parameter
     */
    float FuzzyThresholdParameter;

    /**
     * Fuzzy duration of occurrence
     */
    float FuzzyDurationOfOccurrence;

    /**
     * Character string denoting the units of the field.
     */
    String UnitsOfTheField;

    /**
     * Character string to describe the source of the data.
     */
    String SourceOfTheData;

    /**
     * Title of field.
     */
    String TitleOfField;

    /**
     * The radar number for a single site image (set to zero for a radar
     * composite).
     */
    short RadarNumber;

    /**
     * The radar sites which have gone into forming a composite image. Each site
     * is represented by a particular bit which is set to 1 if the site was
     * available, and 0 if it was not. Radar site 1 will be represented by the
     * least significant bit of element 109.
     */
    short RadarSites;

    /**
     * As element 110 for additional radar sites. This will only be required if
     * the number of operational sites exceeds 16.
     */
    short RadarSites2;

    /**
     * Clutter map number.
     */
    short ClutterMapNumber;

    /**
     * Calibration Type. (0=uncalibrated, 1=frontal, 2=showers, 3=rain shadow,
     * 4=bright band ; the negatives of these values can be used to indicate a
     * calibration which has subsequently been removed.
     */
    short CalibrationType;

    /**
     * Bright band height (units of 10m).
     */
    short BrightBandHeight;

    /**
     * Bright band intensity. This is defined as the enhancement of the rainfall
     * in the bright band relative to the rain beneath it.
     */
    short BrightBandIntensity;

    /**
     * Bright band test parameter 1. This is the percentage of sectors (24 in
     * all) which have detected a possible bright band.
     */
    short BrightBandTestParameter1;

    /**
     * Bright band test parameter 2. This is the percentage of the sectors in
     * entry 30 which agree with the bright band height of 28.
     */
    short BrightBandTestParameter2;

    /**
     * Infill Flag (for level 4.1)
     */
    short InfillFlag;

    /**
     * Stop Elevation (for level 4.1)
     */
    short StopElevation;

    /**
     * Sensor identifier (Satellite data)
     */
    short SensorIdentifier;

    /**
     * Meteosat identifier (currently 5 or 6)
     */
    short MeteosatIdentifier;

    /**
     * Availability of synop meteosat and forecast alphas in combined alphas
     * field (e.g 111 all available, 100, only synop)
     */
    short AvailabilityOfSynopMeteosatAndForecastAlphasInCombinedAlphasField;

    /**
     * Period of interest for accumulation, average or probability (seconds).
     * Only used when element 26 is set to +32767.
     */
    short PeriodOfInterestForAccumulationAverageOrProbability;

    private SARIC_NIMRODHeader() {
    }

    public SARIC_NIMRODHeader(FileInputStream fis, DataInputStream dis) {
        try {
            // i is effectively the element number of the header
            int i;
            i = 0;
            int FortranHousekeeping;
            FortranHousekeeping = dis.readInt();
//            System.out.println("FortranHousekeeping " + FortranHousekeeping);
            i++;
            /**
             * Read general header entries (Bytes 1 - 62, Element numbers 1 to
             * 31).
             */
            VT_year = dis.readShort();
//            System.out.println("VT_year " + VT_year);
            i++;
            VT_month = dis.readShort();
//            System.out.println("VT_month " + VT_month);
            i++;
            VT_day = dis.readShort();
//            System.out.println("VT_day " + VT_day);
            i++;
            VT_hour = dis.readShort();
//            System.out.println("VT_hour " + VT_hour);
            i++;
            VT_minute = dis.readShort();
//            System.out.println("VT_minute " + VT_minute);
            i++;
            VT_second = dis.readShort();
//            System.out.println("VT_second " + VT_second);
            i++;
            DT_year = dis.readShort();
//            System.out.println("DT_year " + DT_year);
            i++;
            DT_month = dis.readShort();
//            System.out.println("DT_month " + DT_month);
            i++;
            DT_day = dis.readShort();
//            System.out.println("DT_day " + DT_day);
            i++;
            DT_hour = dis.readShort();
//            System.out.println("DT_hour " + DT_hour);
            i++;
            DT_minute = dis.readShort();
//            System.out.println("DT_minute " + DT_minute);
            i++;
            dataType = dis.readShort();
//            System.out.println("dataType " + dataType);
            i++;
            NumberOfBytesForEachDataElement = dis.readShort();
//            System.out.println("NumberOfBytesForEachDataElement " + NumberOfBytesForEachDataElement);
            i++;
            ExperimentNumber = dis.readShort();
//            System.out.println("ExperimentNumber " + ExperimentNumber);
            i++;
            HorizontalGridType = dis.readShort();
//            System.out.println("HorizontalGridType " + HorizontalGridType);
            i++;
            nrows = dis.readShort();
//            System.out.println("nrows " + nrows);
            i++;
            ncols = dis.readShort();
//            System.out.println("ncols " + ncols);
            i++;
            HeaderFileReleaseNumber = dis.readShort();
//            System.out.println("HeaderFileReleaseNumber " + HeaderFileReleaseNumber);
            i++;
            FieldCodeNumber = dis.readShort();
//            System.out.println("FieldCodeNumber " + FieldCodeNumber);
            i++;
            VerticalCoordinateType = dis.readShort();
//            System.out.println("VerticalCoordinateType " + VerticalCoordinateType);
            i++;
            VerticalCoordinateOfReferenceLevel = dis.readShort();
//            System.out.println("VerticalCoordinateOfReferenceLevel " + VerticalCoordinateOfReferenceLevel);
            i++;
            NumberOfElementsOfDataSpecificInformationStartingAtElement60 = dis.readShort();
//            System.out.println("NumberOfElementsOfDataSpecificInformationStartingAtElement60 " + NumberOfElementsOfDataSpecificInformationStartingAtElement60);
            i++;
            NumberOfElementsOfDataSpecificInformationStartingAtElement109 = dis.readShort();
//            System.out.println("NumberOfElementsOfDataSpecificInformationStartingAtElement109 " + NumberOfElementsOfDataSpecificInformationStartingAtElement109);
            i++;
            LocationOfOriginOfData = dis.readShort();
//            System.out.println("LocationOfOriginOfData " + LocationOfOriginOfData);
            i++;
            IntegerMissingDataValue = dis.readShort();
//            System.out.println("IntegerMissingDataValue " + IntegerMissingDataValue);
            i++;
            PeriodOfInterestForAccumulationAverageOrProbabilityInMinutes = dis.readShort();
//            System.out.println("PeriodOfInterestForAccumulationAverageOrProbabilityInMinutes " + PeriodOfInterestForAccumulationAverageOrProbabilityInMinutes);
            i++;
            NumberOfModelLevelsAvailableForThisParameter = dis.readShort();
//            System.out.println("NumberOfModelLevelsAvailableForThisParameter " + NumberOfModelLevelsAvailableForThisParameter);
            i++;
            ProjectionBiaxialEllipsoid = dis.readShort();
//            System.out.println("ProjectionBiaxialEllipsoid " + ProjectionBiaxialEllipsoid);
            i++;
            EnsembleMemberID = dis.readShort();
//            System.out.println("EnsembleMemberID " + EnsembleMemberID);
            i++;
            OriginModelID = dis.readShort();
//            System.out.println("OriginModelID " + OriginModelID);
            i++;
            LBPROC = dis.readShort();
//            System.out.println("LBPROC " + LBPROC);
            i++;
            /**
             * Read general header entries (Bytes 63 - 174, Element numbers 32
             * to 59).
             */
            ValueOfVerticalCoordinate = dis.readFloat();
//            System.out.println("ValueOfVerticalCoordinate " + ValueOfVerticalCoordinate);
            i++;
            ValueOfReferenceVerticalCoordinate = dis.readFloat();
//            System.out.println("ValueOfReferenceVerticalCoordinate " + ValueOfReferenceVerticalCoordinate);
            i++;
            NorthingOrLatitudeOrStartLineOfFirstRowOfData = dis.readFloat();
//            System.out.println("NorthingOrLatitudeOrStartLineOfFirstRowOfData " + NorthingOrLatitudeOrStartLineOfFirstRowOfData);
            i++;
            IntervalBetweenRows = dis.readFloat();
//            System.out.println("IntervalBetweenRows " + IntervalBetweenRows);
            i++;
            EastingOrLongitudeOrStartPixelOfFirstPointOfFirstRowOfData = dis.readFloat();
//            System.out.println("EastingOrLongitudeOrStartPixelOfFirstPointOfFirstRowOfData " + EastingOrLongitudeOrStartPixelOfFirstPointOfFirstRowOfData);
            i++;
            IntervalBetweenColumns = dis.readFloat();
//            System.out.println("IntervalBetweenColumns " + IntervalBetweenColumns);
            i++;
            RealMissingDataValue = dis.readFloat();
//            System.out.println("RealMissingDataValue " + RealMissingDataValue);
            i++;
            MKSScalingFactor = dis.readFloat();
//            System.out.println("MKSScalingFactor " + MKSScalingFactor);
            i++;
            DataOffsetValue = dis.readFloat();
//            System.out.println("DataOffsetValue " + DataOffsetValue);
            i++;
            XOffsetOfModelDataFromGridpoints = dis.readFloat();
//            System.out.println("XOffsetOfModelDataFromGridpoints " + XOffsetOfModelDataFromGridpoints);
            i++;
            YOffsetOfModelDataFromGridpoints = dis.readFloat();
//            System.out.println("YOffsetOfModelDataFromGridpoints " + YOffsetOfModelDataFromGridpoints);
            i++;
            StandardLatitudeOrLatitudeOfTrueOrigin = dis.readFloat();
//            System.out.println("StandardLatitudeOrLatitudeOfTrueOrigin " + StandardLatitudeOrLatitudeOfTrueOrigin);
            i++;
            StandardLongitudeOrLongitudeOfTrueOrigin = dis.readFloat();
//            System.out.println("StandardLongitudeOrLongitudeOfTrueOrigin " + StandardLongitudeOrLongitudeOfTrueOrigin);
            i++;
            EastinOfTrueOrigin = dis.readFloat();
//            System.out.println("EastinOfTrueOrigin " + EastinOfTrueOrigin);
            i++;
            NorthingOfTrueOrigin = dis.readFloat();
//            System.out.println("NorthingOfTrueOrigin " + NorthingOfTrueOrigin);
            i++;
            ScaleFactorOnCentralMeridian = dis.readFloat();
//            System.out.println("ScaleFactorOnCentralMeridian " + ScaleFactorOnCentralMeridian);
            i++;
            ThresholdValue = dis.readFloat();
//            System.out.println("ThresholdValue " + ThresholdValue);
            i++;
            for (int j = 0; j < 11; j++) {
                dis.readFloat();
                i++;
            }
//            System.out.println("Element Number " + i);
            /**
             * Read data specific header entries (Bytes 175-354, Element numbers
             * 60 to 104). These elements were previously used for a colour
             * table.
             */
            NorthingOrLatitudeOfTopLeftCornerOfTheImage = dis.readFloat();
//            System.out.println("NorthingOrLatitudeOfTopLeftCornerOfTheImage " + NorthingOrLatitudeOfTopLeftCornerOfTheImage);
            i++;
            EastingOrLongitudeOfTopLeftCornerOfTheImage = dis.readFloat();
//            System.out.println("EastingOrLongitudeOfTopLeftCornerOfTheImage " + EastingOrLongitudeOfTopLeftCornerOfTheImage);
            i++;
            NorthingOrLatitudeOfTopRightCornerOfTheImage = dis.readFloat();
//            System.out.println("NorthingOrLatitudeOfTopRightCornerOfTheImage " + NorthingOrLatitudeOfTopRightCornerOfTheImage);
            i++;
            EastingOrLongitudeOfTopRightCornerOfTheImage = dis.readFloat();
//            System.out.println("EastingOrLongitudeOfTopRightCornerOfTheImage " + EastingOrLongitudeOfTopRightCornerOfTheImage);
            i++;
            NorthingOrLatitudeOfBottomRightCornerOfTheImage = dis.readFloat();
//            System.out.println("NorthingOrLatitudeOfBottomRightCornerOfTheImage " + NorthingOrLatitudeOfBottomRightCornerOfTheImage);
            i++;
            EastingOrLongitudeOfBottomRightCornerOfTheImage = dis.readFloat();
//            System.out.println("EastingOrLongitudeOfBottomRightCornerOfTheImage " + EastingOrLongitudeOfBottomRightCornerOfTheImage);
            i++;
            NorthingOrLatitudeOfBottomLeftCornerOfTheImage = dis.readFloat();
//            System.out.println("NorthingOrLatitudeOfBottomLeftCornerOfTheImage " + NorthingOrLatitudeOfBottomLeftCornerOfTheImage);
            i++;
            EastingOrLongitudeOfBottomLeftCornerOfTheImage = dis.readFloat();
//            System.out.println("EastingOrLongitudeOfBottomLeftCornerOfTheImage " + EastingOrLongitudeOfBottomLeftCornerOfTheImage);
            i++;
            SatelliteCalibrationCoefficient = dis.readFloat();
//            System.out.println("SatelliteCalibrationCoefficient " + SatelliteCalibrationCoefficient);
            i++;
            SpaceCountSatelliteData = dis.readFloat();
//            System.out.println("SpaceCountSatelliteData " + SpaceCountSatelliteData);
            i++;
            DuctingIndex = dis.readFloat();
//            System.out.println("DuctingIndex " + DuctingIndex);
            i++;
            ElevationAngle = dis.readFloat();
//            System.out.println("ElevationAngle " + ElevationAngle);
            i++;
            NeighbourhoodSizeInKmForProbabilities = dis.readFloat();
//            System.out.println("NeighbourhoodSizeInKmForProbabilities " + NeighbourhoodSizeInKmForProbabilities);
            i++;
            RadiusOfInterestInKmForProbabilities = dis.readFloat();
//            System.out.println("RadiusOfInterestInKmForProbabilities " + RadiusOfInterestInKmForProbabilities);
            i++;
            RecursiveFilterStrengthForProbabilities = dis.readFloat();
//            System.out.println("RecursiveFilterStrengthForProbabilities " + RecursiveFilterStrengthForProbabilities);
            i++;
            FuzzyThresholdParameter = dis.readFloat();
//            System.out.println("FuzzyThresholdParameter " + FuzzyThresholdParameter);
            i++;
            FuzzyDurationOfOccurrence = dis.readFloat();
//            System.out.println("FuzzyDurationOfOccurrence " + FuzzyDurationOfOccurrence);
            i++;
            for (int j = 0; j < 28; j++) {
                dis.readFloat();
                i++;
            }
//            System.out.println("Element Number " + i);
            /**
             * Read Character header entries (Bytes 355-410, Element numbers 105
             * to 107).
             */
            try {
                //fis.skip(358L);
                int stringLength;
                char[] c;
                stringLength = 8;
                c = new char[stringLength];
                for (int j = 0; j < stringLength; j++) {
                    c[j] = (char) fis.read();
                }
                UnitsOfTheField = new String(c);
//                System.out.println("UnitsOfTheField " + UnitsOfTheField);
                stringLength = 24;
                c = new char[stringLength];
                for (int j = 0; j < stringLength; j++) {
                    c[j] = (char) fis.read();
                }
                SourceOfTheData = new String(c);
//                System.out.println("SourceOfTheData " + SourceOfTheData);
                c = new char[stringLength];
                for (int j = 0; j < stringLength; j++) {
                    c[j] = (char) fis.read();
                }
                TitleOfField = new String(c);
            } catch (IOException ex) {
                Logger.getLogger(SARIC_NIMRODHeader.class.getName()).log(Level.SEVERE, null, ex);
            }
//            System.out.println("TitleOfField " + TitleOfField);
//            System.out.println("Element Number " + i);
            i += 3;
            /**
             * Read data specific header entries (Bytes 411-512, Element numbers
             * 108 to 159). Table 1: Radar-specific entries.
             */
            RadarNumber = dis.readShort();
//            System.out.println("RadarNumber " + RadarNumber);
            i++;
            RadarSites = dis.readShort();
//            System.out.println("RadarSites " + RadarSites);
            i++;
            RadarSites2 = dis.readShort();
//            System.out.println("RadarSites2 " + RadarSites2);
            i++;
            ClutterMapNumber = dis.readShort();
//            System.out.println("ClutterMapNumber " + ClutterMapNumber);
            i++;
//            System.out.println("Element Number " + i);
            CalibrationType = dis.readShort();
//            System.out.println("CalibrationType " + CalibrationType);
            i++;
            BrightBandHeight = dis.readShort();
//            System.out.println("BrightBandHeight " + BrightBandHeight);
            i++;
            BrightBandIntensity = dis.readShort();
//            System.out.println("BrightBandIntensity " + BrightBandIntensity);
            i++;
            BrightBandTestParameter1 = dis.readShort();
//            System.out.println("BrightBandTestParameter1 " + BrightBandTestParameter1);
            i++;
            BrightBandTestParameter2 = dis.readShort();
//            System.out.println("BrightBandTestParameter2 " + BrightBandTestParameter2);
            i++;
            InfillFlag = dis.readShort();
//            System.out.println("InfillFlag " + InfillFlag);
            i++;
            StopElevation = dis.readShort();
//            System.out.println("StopElevation " + StopElevation);
            i++;
//            System.out.println("Element Number " + i);
            for (int j = 0; j < 13; j++) {
                dis.readShort();//                System.out.println(dis.readShort());
                i++;
            }
//            System.out.println("Element Number " + i);
            for (int j = 0; j < 8; j++) {
//                System.out.println(NorthingOrLatitudeOfTopLeftCornerOfTheImage);
                dis.readShort(); //                System.out.println(dis.readShort());
                i++;
            }
//            System.out.println("Element Number " + i);
            SensorIdentifier = dis.readShort();
//            System.out.println("SensorIdentifier " + SensorIdentifier);
            i++;
            MeteosatIdentifier = dis.readShort();
//            System.out.println("MeteosatIdentifier " + MeteosatIdentifier);
            i++;
            AvailabilityOfSynopMeteosatAndForecastAlphasInCombinedAlphasField = dis.readShort();
//            System.out.println("AvailabilityOfSynopMeteosatAndForecastAlphasInCombinedAlphasField " + AvailabilityOfSynopMeteosatAndForecastAlphasInCombinedAlphasField);
            i++;
//            System.out.println("Element Number " + i);
            for (int j = 0; j < 16; j++) {
//                System.out.println(dis.readShort());
                i++;
            }
//            System.out.println("Element Number " + i);
            PeriodOfInterestForAccumulationAverageOrProbability = dis.readShort();
//            System.out.println("PeriodOfInterestForAccumulationAverageOrProbability " + PeriodOfInterestForAccumulationAverageOrProbability);
            i++;
//            System.out.println("Element Number " + i);
            FortranHousekeeping = dis.readShort();
//            System.out.println("FortranHousekeeping " + FortranHousekeeping);
        } catch (IOException ex) {
            Logger.getLogger(SARIC_NIMRODHeader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        String result = "VT_year " + VT_year;
        result += ", VT_month " + VT_month;
        result += ", VT_day " + VT_day;
        result += ", VT_hour " + VT_hour;
        result += ", VT_minute " + VT_minute;
        result += ", VT_second " + VT_second;
        result += ", DT_year " + DT_year;
        result += ", DT_month " + DT_month;
        result += ", DT_day " + DT_day;
        result += ", DT_hour " + DT_hour;
        result += ", DT_minute " + DT_minute;
        result += ", dataType " + dataType;
        result += ", NumberOfBytesForEachDataElement " + NumberOfBytesForEachDataElement;
        result += ", ExperimentNumber " + ExperimentNumber;
        result += ", HorizontalGridType " + HorizontalGridType;
        result += ", nrows " + nrows;
        result += ", ncols " + ncols;
        result += ", HeaderFileReleaseNumber " + HeaderFileReleaseNumber;
        result += ", FieldCodeNumber " + FieldCodeNumber;
        result += ", VerticalCoordinateType " + VerticalCoordinateType;
        result += ", VerticalCoordinateOfReferenceLevel " + VerticalCoordinateOfReferenceLevel;
        result += ", NumberOfElementsOfDataSpecificInformationStartingAtElement60 " + NumberOfElementsOfDataSpecificInformationStartingAtElement60;
        result += ", NumberOfElementsOfDataSpecificInformationStartingAtElement109 " + NumberOfElementsOfDataSpecificInformationStartingAtElement109;
        result += ", LocationOfOriginOfData " + LocationOfOriginOfData;
        result += ", IntegerMissingDataValue " + IntegerMissingDataValue;
        result += ", PeriodOfInterestForAccumulationAverageOrProbabilityInMinutes " + PeriodOfInterestForAccumulationAverageOrProbabilityInMinutes;
        result += ", NumberOfModelLevelsAvailableForThisParameter " + NumberOfModelLevelsAvailableForThisParameter;
        result += ", ProjectionBiaxialEllipsoid " + ProjectionBiaxialEllipsoid;
        result += ", EnsembleMemberID " + EnsembleMemberID;
        result += ", OriginModelID " + OriginModelID;
        result += ", LBPROC " + LBPROC;
        result += ", ValueOfVerticalCoordinate " + ValueOfVerticalCoordinate;
        result += ", ValueOfReferenceVerticalCoordinate " + ValueOfReferenceVerticalCoordinate;
        result += ", NorthingOrLatitudeOrStartLineOfFirstRowOfData " + NorthingOrLatitudeOrStartLineOfFirstRowOfData;
        result += ", IntervalBetweenRows " + IntervalBetweenRows;
        result += ", EastingOrLongitudeOrStartPixelOfFirstPointOfFirstRowOfData " + EastingOrLongitudeOrStartPixelOfFirstPointOfFirstRowOfData;
        result += ", IntervalBetweenColumns " + IntervalBetweenColumns;
        result += ", RealMissingDataValue " + RealMissingDataValue;
        result += ", MKSScalingFactor " + MKSScalingFactor;
        result += ", DataOffsetValue " + DataOffsetValue;
        result += ", XOffsetOfModelDataFromGridpoints " + XOffsetOfModelDataFromGridpoints;
        result += ", YOffsetOfModelDataFromGridpoints " + YOffsetOfModelDataFromGridpoints;
        result += ", StandardLatitudeOrLatitudeOfTrueOrigin " + StandardLatitudeOrLatitudeOfTrueOrigin;
        result += ", StandardLongitudeOrLongitudeOfTrueOrigin " + StandardLongitudeOrLongitudeOfTrueOrigin;
        result += ", EastinOfTrueOrigin " + EastinOfTrueOrigin;
        result += ", NorthingOfTrueOrigin " + NorthingOfTrueOrigin;
        result += ", ScaleFactorOnCentralMeridian " + ScaleFactorOnCentralMeridian;
        result += ", ThresholdValue " + ThresholdValue;
        result += ", NorthingOrLatitudeOfTopLeftCornerOfTheImage " + NorthingOrLatitudeOfTopLeftCornerOfTheImage;
        result += ", EastingOrLongitudeOfTopLeftCornerOfTheImage " + EastingOrLongitudeOfTopLeftCornerOfTheImage;
        result += ", NorthingOrLatitudeOfTopRightCornerOfTheImage " + NorthingOrLatitudeOfTopRightCornerOfTheImage;
        result += ", EastingOrLongitudeOfTopRightCornerOfTheImage " + EastingOrLongitudeOfTopRightCornerOfTheImage;
        result += ", NorthingOrLatitudeOfBottomRightCornerOfTheImage " + NorthingOrLatitudeOfBottomRightCornerOfTheImage;
        result += ", EastingOrLongitudeOfBottomRightCornerOfTheImage " + EastingOrLongitudeOfBottomRightCornerOfTheImage;
        result += ", NorthingOrLatitudeOfBottomLeftCornerOfTheImage " + NorthingOrLatitudeOfBottomLeftCornerOfTheImage;
        result += ", EastingOrLongitudeOfBottomLeftCornerOfTheImage " + EastingOrLongitudeOfBottomLeftCornerOfTheImage;
        result += ", SatelliteCalibrationCoefficient " + SatelliteCalibrationCoefficient;
        result += ", SpaceCountSatelliteData " + SpaceCountSatelliteData;
        result += ", DuctingIndex " + DuctingIndex;
        result += ", ElevationAngle " + ElevationAngle;
        result += ", NeighbourhoodSizeInKmForProbabilities " + NeighbourhoodSizeInKmForProbabilities;
        result += ", RadiusOfInterestInKmForProbabilities " + RadiusOfInterestInKmForProbabilities;
        result += ", RecursiveFilterStrengthForProbabilities " + RecursiveFilterStrengthForProbabilities;
        result += ", FuzzyThresholdParameter " + FuzzyThresholdParameter;
        result += ", FuzzyDurationOfOccurrence " + FuzzyDurationOfOccurrence;
        result += ", UnitsOfTheField " + UnitsOfTheField;
        result += ", SourceOfTheData " + SourceOfTheData;
        result += ", TitleOfField " + TitleOfField;
        result += ", RadarNumber " + RadarNumber;
        result += ", RadarSites " + RadarSites;
        result += ", RadarSites2 " + RadarSites2;
        result += ", ClutterMapNumber " + ClutterMapNumber;
        result += ", CalibrationType " + CalibrationType;
        result += ", BrightBandHeight " + BrightBandHeight;
        result += ", BrightBandIntensity " + BrightBandIntensity;
        result += ", BrightBandTestParameter1 " + BrightBandTestParameter1;
        result += ", BrightBandTestParameter2 " + BrightBandTestParameter2;
        result += ", InfillFlag " + InfillFlag;
        result += ", StopElevation " + StopElevation;
        result += ", SensorIdentifier " + SensorIdentifier;
        result += ", MeteosatIdentifier " + MeteosatIdentifier;
        result += ", AvailabilityOfSynopMeteosatAndForecastAlphasInCombinedAlphasField " + AvailabilityOfSynopMeteosatAndForecastAlphasInCombinedAlphasField;
        result += ", PeriodOfInterestForAccumulationAverageOrProbability " + PeriodOfInterestForAccumulationAverageOrProbability;
        return result;
    }
}
