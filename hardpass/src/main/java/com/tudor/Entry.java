package com.tudor;

import java.util.UUID;

public class Entry {
    
    private UUID m_id;
    private String m_entryName;
    private UUID m_categoryId;
    private String m_additionalNote;

    public Entry(String entryName, String additionalNote)
    {
        this.m_id = UUID.randomUUID();
        this.m_entryName = entryName;
        this.m_additionalNote = additionalNote;
    }

    // GETS

    public UUID getId() {return m_id;};
    public String getEntryName() {return m_entryName;};
    public UUID getCategoryId() {return m_categoryId;};
    public String getAdditionalNote() {return m_additionalNote;};

    // SETS

    public void setEntryName(String value) {m_entryName = value;};
    public void setCategoryId(UUID value) {m_categoryId = value;};
    public void setAdditionalNote(String value) {m_additionalNote = value;};


}
