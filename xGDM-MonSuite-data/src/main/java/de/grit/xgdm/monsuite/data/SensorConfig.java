/*-
 * #%L
 * xGDM-MonSuite (Data Binding)
 * %%
 * Copyright (C) 2022 - 2025 grit GmbH
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package de.grit.xgdm.monsuite.data;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import io.ebean.annotation.JsonIgnore;

/**
 * Embedded SensorConfig to be replaced later
 * <p>
 * The Column mapping is made in the Sensor class
 * 
 * @see de.grit.xgdm.monsuite.data.Sensor
 * 
 * @author <a href="mailto:reichhelm@grit.de">Stephan Reichhelm</a>
 */
@Embeddable
public class SensorConfig implements Serializable, Cloneable {

    private static final long serialVersionUID = 819778456735828516L;

    @Column(name = "URL", length = 500)
    private String url;

    @Column(name = "LAYER_AVAIL", length = 500)
    private String layerAvail;

    @Column(name = "VERSION_STRING", length = 100)
    private String versionString;

    @Column(name = "LAYER_IMGCHECK", length = 500)
    private String layerImgCheck;

    @Column(name = "HTTP_USER", length = 100)
    private String httpUser;

    @Column(name = "HTTP_PASS", length = 100)
    private String httpPass;

    @JsonIgnore
    @Column(name = "IMS_PIPE", length = 100)
    private String imsPipe;

    @JsonIgnore
    @Column(name = "IMS_HOST", length = 100)
    private String imsHost;

    @JsonIgnore
    @Column(name = "IMS_ACTION")
    private Integer imsAction;

    @Column(name = "APP_USER", length = 100)
    private String appUser;

    @Column(name = "APP_PASS", length = 100)
    private String appPass;

    @JsonIgnore
    @Column(name = "DBP_DATABASE", length = 200)
    private String dbpDatabase;

    @JsonIgnore
    @Column(name = "DBP_ACTION")
    private Integer dbpAction;

    @Column(name = "FEATURE_CAP", length = 1500)
    private String featureCap;

    @Column(name = "FEATURE_GET", length = 1500)
    private String featureGet;

    @Column(name = "PROXY", length = 250)
    private String proxy;

    @Column(name = "SRS", length = 250)
    private String srs;

    @Column(name = "BBOX", length = 500)
    private String bbox;

    @Column(name = "STYLES_IMGCHECK", length = 500)
    private String stylesImgCheck;

    @JsonIgnore
    @Column(name = "FORMAT_IMGCHECK", length = 500)
    private String formatImgCheck;

    @Column(name = "REGEXP_REQ", length = 500)
    private String regexpReq;

    @Column(name = "REGEXP_NOT", length = 500)
    private String regexpNot;

    @Column(name = "VALID_CODE", length = 250)
    private String validCode;

    @JsonIgnore
    @Column(name = "IMAGE_SIZE", length = 150)
    private String imageSize;

    @JsonIgnore
    @Column(name = "IMAGE_DIR", length = 150)
    private String imageDir;

    @Column(name = "SQL_CMD", length = 250)
    private String sqlCmd;

    @JsonIgnore
    @Column(name = "MAP_NAME_ID")
    private Integer mapNameId;

    @JsonIgnore
    @Column(name = "SCALE")
    private Integer scale;

    @Column(name = "ROTATION")
    private Integer rotation;

    @JsonIgnore
    @Column(name = "CENTER_POS", length = 250)
    private String centerPos;

    @JsonIgnore
    @Column(name = "STRING_PROP", length = 1000)
    private String stringProp;

    @JsonIgnore
    @Column(name = "RES_DPI")
    private Integer resDpi;

    @JsonIgnore
    @Column(name = "FMT_NAME", length = 50)
    private String fmtName;

    @JsonIgnore
    @Column(name = "MAP_MXD", length = 250)
    private String mapMxd;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "SensorConfig [url=" );
        builder.append( url );
        builder.append( ", layerAvail=" );
        builder.append( layerAvail );
        builder.append( ", versionString=" );
        builder.append( versionString );
        builder.append( ", layerImgCheck=" );
        builder.append( layerImgCheck );
        builder.append( ", httpUser=" );
        builder.append( httpUser );
        builder.append( ", httpPass=" );
        builder.append( httpPass );
        builder.append( ", imsPipe=" );
        builder.append( imsPipe );
        builder.append( ", imsHost=" );
        builder.append( imsHost );
        builder.append( ", imsAction=" );
        builder.append( imsAction );
        builder.append( ", appUser=" );
        builder.append( appUser );
        builder.append( ", appPass=" );
        builder.append( appPass );
        builder.append( ", dbpDatabase=" );
        builder.append( dbpDatabase );
        builder.append( ", dbpAction=" );
        builder.append( dbpAction );
        builder.append( ", featureCap=" );
        builder.append( featureCap );
        builder.append( ", featureGet=" );
        builder.append( featureGet );
        builder.append( ", proxy=" );
        builder.append( proxy );
        builder.append( ", srs=" );
        builder.append( srs );
        builder.append( ", bbox=" );
        builder.append( bbox );
        builder.append( ", stylesImgCheck=" );
        builder.append( stylesImgCheck );
        builder.append( ", formatImgCheck=" );
        builder.append( formatImgCheck );
        builder.append( ", regexpReq=" );
        builder.append( regexpReq );
        builder.append( ", regexpNot=" );
        builder.append( regexpNot );
        builder.append( ", validCode=" );
        builder.append( validCode );
        builder.append( ", imageSize=" );
        builder.append( imageSize );
        builder.append( ", imageDir=" );
        builder.append( imageDir );
        builder.append( ", sqlCmd=" );
        builder.append( sqlCmd );
        builder.append( ", mapNameId=" );
        builder.append( mapNameId );
        builder.append( ", scale=" );
        builder.append( scale );
        builder.append( ", rotation=" );
        builder.append( rotation );
        builder.append( ", centerPos=" );
        builder.append( centerPos );
        builder.append( ", stringProp=" );
        builder.append( stringProp );
        builder.append( ", resDpi=" );
        builder.append( resDpi );
        builder.append( ", fmtName=" );
        builder.append( fmtName );
        builder.append( ", mapMxd=" );
        builder.append( mapMxd );
        builder.append( ", version=" );
        builder.append( versionString );
        builder.append( "]" );
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash( appPass, appUser, bbox, centerPos, dbpAction, dbpDatabase, featureCap, //
                             featureGet, fmtName, formatImgCheck, httpPass, httpUser, imageDir, imageSize, //
                             imsAction, imsHost, imsPipe, layerAvail, layerImgCheck, mapMxd, mapNameId, //
                             proxy, regexpNot, regexpReq, resDpi, rotation, scale, sqlCmd, srs, stringProp, //
                             stylesImgCheck, url, validCode, versionString );
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( !( obj instanceof SensorConfig ) ) {
            return false;
        }

        SensorConfig other = (SensorConfig) obj;
        if ( Objects.equals( appPass, other.appPass ) && // all other property by a-z
             Objects.equals( appUser, other.appUser ) && //
             Objects.equals( bbox, other.bbox ) && //
             Objects.equals( centerPos, other.centerPos ) && //
             Objects.equals( dbpAction, other.dbpAction ) && //
             Objects.equals( dbpDatabase, other.dbpDatabase ) && //
             Objects.equals( featureCap, other.featureCap ) && //
             Objects.equals( featureGet, other.featureGet ) && //
             Objects.equals( fmtName, other.fmtName ) && //
             Objects.equals( formatImgCheck, other.formatImgCheck ) && //
             Objects.equals( httpPass, other.httpPass ) && //
             Objects.equals( httpUser, other.httpUser ) && //
             Objects.equals( imageDir, other.imageDir ) && //
             Objects.equals( imageSize, other.imageSize ) && //
             Objects.equals( imsAction, other.imsAction ) && //
             Objects.equals( imsHost, other.imsHost ) && //
             Objects.equals( imsPipe, other.imsPipe ) && //
             Objects.equals( layerAvail, other.layerAvail ) && //
             Objects.equals( layerImgCheck, other.layerImgCheck ) && //
             Objects.equals( mapMxd, other.mapMxd ) && //
             Objects.equals( mapNameId, other.mapNameId ) && //
             Objects.equals( proxy, other.proxy ) && //
             Objects.equals( regexpNot, other.regexpNot ) && //
             Objects.equals( regexpReq, other.regexpReq ) && //
             Objects.equals( resDpi, other.resDpi ) && //
             Objects.equals( rotation, other.rotation ) && //
             Objects.equals( scale, other.scale ) && //
             Objects.equals( sqlCmd, other.sqlCmd ) && //
             Objects.equals( srs, other.srs ) && //
             Objects.equals( stringProp, other.stringProp ) && //
             Objects.equals( stylesImgCheck, other.stylesImgCheck ) && //
             Objects.equals( url, other.url ) && //
             Objects.equals( validCode, other.validCode ) && //
             Objects.equals( versionString, other.versionString ) )
            return true;
        else
            return false;
    }

    public SensorConfig clone() {
        SensorConfig sc = new SensorConfig();

        sc.setAppPass( this.getAppPass() );
        sc.setAppUser( this.getAppUser() );
        sc.setBbox( this.getBbox() );
        sc.setCenterPos( this.getCenterPos() );
        sc.setDbpAction( this.getDbpAction() );
        sc.setDbpDatabase( this.getDbpDatabase() );
        sc.setFeatureCap( this.getFeatureCap() );
        sc.setFeatureGet( this.getFeatureGet() );
        sc.setFmtName( this.getFmtName() );
        sc.setFormatImgCheck( this.getFormatImgCheck() );
        sc.setHttpPass( this.getHttpPass() );
        sc.setHttpUser( this.getHttpUser() );
        sc.setImageDir( this.getImageDir() );
        sc.setImageSize( this.getImageSize() );
        sc.setImsAction( this.getImsAction() );
        sc.setImsHost( this.getImsHost() );
        sc.setImsPipe( this.getImsPipe() );
        sc.setLayerAvail( this.getLayerAvail() );
        sc.setLayerImgCheck( this.getLayerImgCheck() );
        sc.setMapMxd( this.getMapMxd() );
        sc.setMapNameId( this.getMapNameId() );
        sc.setProxy( this.getProxy() );
        sc.setRegexpNot( this.getRegexpNot() );
        sc.setRegexpReq( this.getRegexpReq() );
        sc.setResDpi( this.getResDpi() );
        sc.setRotation( this.getRotation() );
        sc.setScale( this.getScale() );
        sc.setSqlCmd( this.getSqlCmd() );
        sc.setSrs( this.getSrs() );
        sc.setStringProp( this.getStringProp() );
        sc.setStylesImgCheck( this.getStylesImgCheck() );
        sc.setUrl( this.getUrl() );
        sc.setValidCode( this.getValidCode() );
        sc.setVersionString( this.getVersionString() );

        return sc;
    }

    public void update( SensorConfig update ) {
        if ( update.getAppPass() != null )
            this.setAppPass( update.getAppPass() );
        if ( update.getAppUser() != null )
            this.setAppUser( update.getAppUser() );
        if ( update.getBbox() != null )
            this.setBbox( update.getBbox() );
        if ( update.getCenterPos() != null )
            this.setCenterPos( update.getCenterPos() );
        if ( update.getDbpAction() != null )
            this.setDbpAction( update.getDbpAction() );
        if ( update.getDbpDatabase() != null )
            this.setDbpDatabase( update.getDbpDatabase() );
        if ( update.getFeatureCap() != null )
            this.setFeatureCap( update.getFeatureCap() );
        if ( update.getFeatureGet() != null )
            this.setFeatureGet( update.getFeatureGet() );
        if ( update.getFmtName() != null )
            this.setFmtName( update.getFmtName() );
        if ( update.getFormatImgCheck() != null )
            this.setFormatImgCheck( update.getFormatImgCheck() );
        if ( update.getHttpPass() != null )
            this.setHttpPass( update.getHttpPass() );
        if ( update.getHttpUser() != null )
            this.setHttpUser( update.getHttpUser() );
        if ( update.getImageDir() != null )
            this.setImageDir( update.getImageDir() );
        if ( update.getImageSize() != null )
            this.setImageSize( update.getImageSize() );
        if ( update.getImsAction() != null )
            this.setImsAction( update.getImsAction() );
        if ( update.getImsHost() != null )
            this.setImsHost( update.getImsHost() );
        if ( update.getImsPipe() != null )
            this.setImsPipe( update.getImsPipe() );
        if ( update.getLayerAvail() != null )
            this.setLayerAvail( update.getLayerAvail() );
        if ( update.getLayerImgCheck() != null )
            this.setLayerImgCheck( update.getLayerImgCheck() );
        if ( update.getMapMxd() != null )
            this.setMapMxd( update.getMapMxd() );
        if ( update.getMapNameId() != null )
            this.setMapNameId( update.getMapNameId() );
        if ( update.getProxy() != null )
            this.setProxy( update.getProxy() );
        if ( update.getRegexpNot() != null )
            this.setRegexpNot( update.getRegexpNot() );
        if ( update.getRegexpReq() != null )
            this.setRegexpReq( update.getRegexpReq() );
        if ( update.getResDpi() != null )
            this.setResDpi( update.getResDpi() );
        if ( update.getRotation() != null )
            this.setRotation( update.getRotation() );
        if ( update.getScale() != null )
            this.setScale( update.getScale() );
        if ( update.getSqlCmd() != null )
            this.setSqlCmd( update.getSqlCmd() );
        if ( update.getSrs() != null )
            this.setSrs( update.getSrs() );
        if ( update.getStringProp() != null )
            this.setStringProp( update.getStringProp() );
        if ( update.getStylesImgCheck() != null )
            this.setStylesImgCheck( update.getStylesImgCheck() );
        if ( update.getUrl() != null )
            this.setUrl( update.getUrl() );
        if ( update.getValidCode() != null )
            this.setValidCode( update.getValidCode() );
        if ( update.getVersionString() != null )
            this.setVersionString( update.getVersionString() );
    }

    public String getUrl() {
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
    }

    public String getLayerAvail() {
        return layerAvail;
    }

    public void setLayerAvail( String layerAvail ) {
        this.layerAvail = layerAvail;
    }

    public String getVersionString() {
        return versionString;
    }

    public void setVersionString( String versionString ) {
        this.versionString = versionString;
    }

    public String getLayerImgCheck() {
        return layerImgCheck;
    }

    public void setLayerImgCheck( String layerImgCheck ) {
        this.layerImgCheck = layerImgCheck;
    }

    public String getHttpUser() {
        return httpUser;
    }

    public void setHttpUser( String httpUser ) {
        this.httpUser = httpUser;
    }

    public String getHttpPass() {
        return httpPass;
    }

    public void setHttpPass( String httpPass ) {
        this.httpPass = httpPass;
    }

    public String getImsPipe() {
        return imsPipe;
    }

    public void setImsPipe( String imsPipe ) {
        this.imsPipe = imsPipe;
    }

    public String getImsHost() {
        return imsHost;
    }

    public void setImsHost( String imsHost ) {
        this.imsHost = imsHost;
    }

    public Integer getImsAction() {
        return imsAction;
    }

    public void setImsAction( Integer imsAction ) {
        this.imsAction = imsAction;
    }

    public String getAppUser() {
        return appUser;
    }

    public void setAppUser( String appUser ) {
        this.appUser = appUser;
    }

    public String getAppPass() {
        return appPass;
    }

    public void setAppPass( String appPass ) {
        this.appPass = appPass;
    }

    public String getDbpDatabase() {
        return dbpDatabase;
    }

    public void setDbpDatabase( String dbpDatabase ) {
        this.dbpDatabase = dbpDatabase;
    }

    public Integer getDbpAction() {
        return dbpAction;
    }

    public void setDbpAction( Integer dbpAction ) {
        this.dbpAction = dbpAction;
    }

    public String getFeatureCap() {
        return featureCap;
    }

    public void setFeatureCap( String featureCap ) {
        this.featureCap = featureCap;
    }

    public String getFeatureGet() {
        return featureGet;
    }

    public void setFeatureGet( String featureGet ) {
        this.featureGet = featureGet;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy( String proxy ) {
        this.proxy = proxy;
    }

    public String getSrs() {
        return srs;
    }

    public void setSrs( String srs ) {
        this.srs = srs;
    }

    public String getBbox() {
        return bbox;
    }

    public void setBbox( String bbox ) {
        this.bbox = bbox;
    }

    public String getStylesImgCheck() {
        return stylesImgCheck;
    }

    public void setStylesImgCheck( String stylesImgCheck ) {
        this.stylesImgCheck = stylesImgCheck;
    }

    public String getFormatImgCheck() {
        return formatImgCheck;
    }

    public void setFormatImgCheck( String formatImgCheck ) {
        this.formatImgCheck = formatImgCheck;
    }

    public String getRegexpReq() {
        return regexpReq;
    }

    public void setRegexpReq( String regexpReq ) {
        this.regexpReq = regexpReq;
    }

    public String getRegexpNot() {
        return regexpNot;
    }

    public void setRegexpNot( String regexpNot ) {
        this.regexpNot = regexpNot;
    }

    public String getValidCode() {
        return validCode;
    }

    public void setValidCode( String validCode ) {
        this.validCode = validCode;
    }

    public String getImageSize() {
        return imageSize;
    }

    public void setImageSize( String imageSize ) {
        this.imageSize = imageSize;
    }

    public String getImageDir() {
        return imageDir;
    }

    public void setImageDir( String imageDir ) {
        this.imageDir = imageDir;
    }

    public String getSqlCmd() {
        return sqlCmd;
    }

    public void setSqlCmd( String sqlCmd ) {
        this.sqlCmd = sqlCmd;
    }

    public Integer getMapNameId() {
        return mapNameId;
    }

    public void setMapNameId( Integer mapNameId ) {
        this.mapNameId = mapNameId;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale( Integer scale ) {
        this.scale = scale;
    }

    public Integer getRotation() {
        return rotation;
    }

    public void setRotation( Integer rotation ) {
        this.rotation = rotation;
    }

    public String getCenterPos() {
        return centerPos;
    }

    public void setCenterPos( String centerPos ) {
        this.centerPos = centerPos;
    }

    public String getStringProp() {
        return stringProp;
    }

    public void setStringProp( String stringProp ) {
        this.stringProp = stringProp;
    }

    public Integer getResDpi() {
        return resDpi;
    }

    public void setResDpi( Integer resDpi ) {
        this.resDpi = resDpi;
    }

    public String getFmtName() {
        return fmtName;
    }

    public void setFmtName( String fmtName ) {
        this.fmtName = fmtName;
    }

    public String getMapMxd() {
        return mapMxd;
    }

    public void setMapMxd( String mapMxd ) {
        this.mapMxd = mapMxd;
    }
}
