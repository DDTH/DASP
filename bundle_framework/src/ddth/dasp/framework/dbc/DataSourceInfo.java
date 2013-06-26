package ddth.dasp.framework.dbc;

import java.util.concurrent.atomic.AtomicLong;

public class DataSourceInfo {
    private String name;
    private int numActives = -1, maxActives = -1, numIdles = -1, minIdles = -1, maxIdles = -1;
    private long maxWait = -1;
    private AtomicLong numOpens = new AtomicLong(), numCloses = new AtomicLong(),
            numLeakCloses = new AtomicLong();

    public DataSourceInfo() {
    }

    public DataSourceInfo(String name) {
        this.name = name;
    }

    public long getNumOpens() {
        return numOpens.get();
    }

    public long incNumOpens() {
        return numOpens.incrementAndGet();
    }

    public long getNumCloses() {
        return numCloses.get();
    }

    public long incNumCloses() {
        return numCloses.incrementAndGet();
    }

    public long getNumLeakCloses() {
        return numLeakCloses.get();
    }

    public long incNumLeakCloses() {
        return numLeakCloses.incrementAndGet();
    }

    public String getName() {
        return name;
    }

    public DataSourceInfo setName(String name) {
        this.name = name;
        return this;
    }

    public int getNumActives() {
        return numActives;
    }

    public DataSourceInfo setNumActives(int numActives) {
        this.numActives = numActives;
        return this;
    }

    public int getMaxActives() {
        return maxActives;
    }

    public DataSourceInfo setMaxActives(int maxActives) {
        this.maxActives = maxActives;
        return this;
    }

    public int getNumIdles() {
        return numIdles;
    }

    public DataSourceInfo setNumIdles(int numIdles) {
        this.numIdles = numIdles;
        return this;
    }

    public int getMinIdles() {
        return minIdles;
    }

    public DataSourceInfo setMinIdles(int minIdles) {
        this.minIdles = minIdles;
        return this;
    }

    public int getMaxIdles() {
        return maxIdles;
    }

    public DataSourceInfo setMaxIdles(int maxIdles) {
        this.maxIdles = maxIdles;
        return this;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public DataSourceInfo setMaxWait(long maxWait) {
        this.maxWait = maxWait;
        return this;
    }
}
