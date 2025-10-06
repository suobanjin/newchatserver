package zzuli.zw.config.pojo;

public class PageParam {
    private int page;   // 页码
    private int size;   // 每页数量

    public PageParam(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public int getOffset() {
        return (page - 1) * size;
    }

    public int getLimit() {
        return size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}

