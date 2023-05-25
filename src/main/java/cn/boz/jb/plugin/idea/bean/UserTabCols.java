package cn.boz.jb.plugin.idea.bean;

import java.sql.Timestamp;

public class UserTabCols {
    private String tableName;
    private String columnName;
    private String dataType;
    private int dataLength;
    private int dataPrecision;
    private int dataScale;
    private String nullable;
    private int columnId;
    private int defaultLength;
    private String dataDefault;
    private long numDistinct;
    private String lowValue;
    private String highValue;
    private double density;
    private long numNulls;
    private long numBuckets;
    private Timestamp lastAnalyzed;
    private long sampleSize;
    private String characterSetName;
    private long charColDeclLength;
    private String globalStats;
    private String userStats;
    private String dataSetName;
    private String columnUsage;
    private String virtualColumn;
    private String generated;
    private String identityColumn;
     // Getters and Setters

    private String comments;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public int getDataPrecision() {
        return dataPrecision;
    }

    public void setDataPrecision(int dataPrecision) {
        this.dataPrecision = dataPrecision;
    }

    public int getDataScale() {
        return dataScale;
    }

    public void setDataScale(int dataScale) {
        this.dataScale = dataScale;
    }

    public String getNullable() {
        return nullable;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

    public int getColumnId() {
        return columnId;
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }

    public int getDefaultLength() {
        return defaultLength;
    }

    public void setDefaultLength(int defaultLength) {
        this.defaultLength = defaultLength;
    }

    public String getDataDefault() {
        return dataDefault;
    }

    public void setDataDefault(String dataDefault) {
        this.dataDefault = dataDefault;
    }

    public long getNumDistinct() {
        return numDistinct;
    }

    public void setNumDistinct(long numDistinct) {
        this.numDistinct = numDistinct;
    }

    public String getLowValue() {
        return lowValue;
    }

    public void setLowValue(String lowValue) {
        this.lowValue = lowValue;
    }

    public String getHighValue() {
        return highValue;
    }

    public void setHighValue(String highValue) {
        this.highValue = highValue;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public long getNumNulls() {
        return numNulls;
    }

    public void setNumNulls(long numNulls) {
        this.numNulls = numNulls;
    }

    public long getNumBuckets() {
        return numBuckets;
    }

    public void setNumBuckets(long numBuckets) {
        this.numBuckets = numBuckets;
    }

    public Timestamp getLastAnalyzed() {
        return lastAnalyzed;
    }

    public void setLastAnalyzed(Timestamp lastAnalyzed) {
        this.lastAnalyzed = lastAnalyzed;
    }

    public long getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(long sampleSize) {
        this.sampleSize = sampleSize;
    }

    public String getCharacterSetName() {
        return characterSetName;
    }

    public void setCharacterSetName(String characterSetName) {
        this.characterSetName = characterSetName;
    }

    public long getCharColDeclLength() {
        return charColDeclLength;
    }

    public void setCharColDeclLength(long charColDeclLength) {
        this.charColDeclLength = charColDeclLength;
    }

    public String getGlobalStats() {
        return globalStats;
    }

    public void setGlobalStats(String globalStats) {
        this.globalStats = globalStats;
    }

    public String getUserStats() {
        return userStats;
    }

    public void setUserStats(String userStats) {
        this.userStats = userStats;
    }

    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public String getColumnUsage() {
        return columnUsage;
    }

    public void setColumnUsage(String columnUsage) {
        this.columnUsage = columnUsage;
    }

    public String getVirtualColumn() {
        return virtualColumn;
    }

    public void setVirtualColumn(String virtualColumn) {
        this.virtualColumn = virtualColumn;
    }

    public String getGenerated() {
        return generated;
    }

    public void setGenerated(String generated) {
        this.generated = generated;
    }

    public String getIdentityColumn() {
        return identityColumn;
    }

    public void setIdentityColumn(String identityColumn) {
        this.identityColumn = identityColumn;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
