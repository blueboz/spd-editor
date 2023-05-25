package cn.boz.jb.plugin.idea.listener;

public interface ProcessSaveListener {

    /**
     * supply with data and do what you want to do
     */
    void save(byte[] data);
}
