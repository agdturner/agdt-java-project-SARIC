package uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.nimrod;

import java.io.DataInputStream;
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
     * The radar number for a single site image (set to zero for a radar composite).
     */
    short RadarNumber;
    
    /**
     * The radar sites which have gone into forming a composite image. Each site is represented by a particular bit which is set to 1 if the site was available, and 0 if it was not. Radar site 1 will be represented by the least significant bit of element 109.
     */
    short RadarSites;
    
    /**
     * As element 110 for additional radar sites. This will only be required if the number of operational sites exceeds 16.
     */
    short RadarSites2;
    
    /**
     * Clutter map number.
     */
    short ClutterMapNumber;
    
    /**
     * 
     */
    
    
    
    
    
    
    
    
    private SARIC_NIMRODHeader() {
    }

    public SARIC_NIMRODHeader(DataInputStream dis) {
        try {
            int FortranHousekeeping;
            FortranHousekeeping = dis.readInt();
            System.out.println("FortranHousekeeping " + FortranHousekeeping);
            VT_year = dis.readShort();
            VT_month = dis.readShort();
            VT_day = dis.readShort();
            VT_hour = dis.readShort();
            VT_minute = dis.readShort();
            VT_second = dis.readShort();
            DT_year = dis.readShort();
            DT_month = dis.readShort();
            DT_day = dis.readShort();
            DT_hour = dis.readShort();
            DT_minute = dis.readShort();
            dataType = dis.readShort();
            System.out.println("dataType " + dataType);
            NumberOfBytesForEachDataElement = dis.readShort();
            System.out.println("NumberOfBytesForEachDataElement " + NumberOfBytesForEachDataElement);
            ExperimentNumber = dis.readShort();
            System.out.println("ExperimentNumber " + ExperimentNumber);
            HorizontalGridType = dis.readShort();
            nrows = dis.readShort();
            System.out.println("nrows " + nrows);
            ncols = dis.readShort();
            System.out.println("ncols " + ncols);
            HeaderFileReleaseNumber = dis.readShort();
            FieldCodeNumber = dis.readShort();
            VerticalCoordinateType = dis.readShort();
            VerticalCoordinateOfReferenceLevel = dis.readShort();
            NumberOfElementsOfDataSpecificInformationStartingAtElement60 = dis.readShort();
            NumberOfElementsOfDataSpecificInformationStartingAtElement109 = dis.readShort();
            LocationOfOriginOfData = dis.readShort();
            IntegerMissingDataValue = dis.readShort();
            PeriodOfInterestForAccumulationAverageOrProbabilityInMinutes = dis.readShort();
            NumberOfModelLevelsAvailableForThisParameter = dis.readShort();
            ProjectionBiaxialEllipsoid = dis.readShort();
            EnsembleMemberID = dis.readShort();
            OriginModelID = dis.readShort();
            LBPROC = dis.readShort();
            ValueOfVerticalCoordinate = dis.readFloat();
            ValueOfReferenceVerticalCoordinate = dis.readFloat();
            NorthingOrLatitudeOrStartLineOfFirstRowOfData = dis.readFloat();
            IntervalBetweenRows = dis.readFloat();
            EastingOrLongitudeOrStartPixelOfFirstPointOfFirstRowOfData = dis.readFloat();
            IntervalBetweenColumns = dis.readFloat();
            RealMissingDataValue = dis.readFloat();
            MKSScalingFactor = dis.readFloat();
            DataOffsetValue = dis.readFloat();
            XOffsetOfModelDataFromGridpoints = dis.readFloat();
            YOffsetOfModelDataFromGridpoints = dis.readFloat();
            StandardLatitudeOrLatitudeOfTrueOrigin = dis.readFloat();
            StandardLongitudeOrLongitudeOfTrueOrigin = dis.readFloat();
            EastinOfTrueOrigin = dis.readFloat();
            NorthingOfTrueOrigin = dis.readFloat();
            ScaleFactorOnCentralMeridian = dis.readFloat();
            ThresholdValue = dis.readFloat();
            dis.readFloat();
            dis.readFloat();
            NorthingOrLatitudeOfTopLeftCornerOfTheImage = dis.readFloat();
            EastingOrLongitudeOfTopLeftCornerOfTheImage = dis.readFloat();
            NorthingOrLatitudeOfTopRightCornerOfTheImage = dis.readFloat();
            EastingOrLongitudeOfTopRightCornerOfTheImage = dis.readFloat();
            NorthingOrLatitudeOfBottomRightCornerOfTheImage = dis.readFloat();
            EastingOrLongitudeOfBottomRightCornerOfTheImage = dis.readFloat();
            NorthingOrLatitudeOfBottomLeftCornerOfTheImage = dis.readFloat();
            EastingOrLongitudeOfBottomLeftCornerOfTheImage = dis.readFloat();
            SatelliteCalibrationCoefficient = dis.readFloat();
            SpaceCountSatelliteData = dis.readFloat();
            DuctingIndex = dis.readFloat();
            ElevationAngle = dis.readFloat();
            NeighbourhoodSizeInKmForProbabilities = dis.readFloat();
            RadiusOfInterestInKmForProbabilities = dis.readFloat();
            RecursiveFilterStrengthForProbabilities = dis.readFloat();
            FuzzyThresholdParameter = dis.readFloat();
            FuzzyDurationOfOccurrence = dis.readFloat();
            for (int i = 0; i < 28; i++) {
                dis.readFloat();
            }
            int stringLength;
            char[] c;
            stringLength = 4;
            c = new char[stringLength];
            for (int i = 0; i < stringLength; i++) {
                c[i] = dis.readChar();
            }
            UnitsOfTheField = new String(c);
            System.out.println("UnitsOfTheField " + UnitsOfTheField);
            stringLength = 12;
            c = new char[stringLength];
            for (int i = 0; i < stringLength; i++) {
                c[i] = dis.readChar();
            }
            SourceOfTheData = new String(c);
            System.out.println("SourceOfTheData " + SourceOfTheData);
            c = new char[stringLength];
            for (int i = 0; i < stringLength; i++) {
                c[i] = dis.readChar();
            }
            TitleOfField = new String(c);
            System.out.println("TitleOfField " + TitleOfField);
        } catch (IOException ex) {
            Logger.getLogger(SARIC_NIMRODHeader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
