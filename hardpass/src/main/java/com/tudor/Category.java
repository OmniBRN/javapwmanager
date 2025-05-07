package main.java.com.tudor;

import java.util.UUID;

public class Category {
    private UUID m_id;
    private String m_categoryName;

    public Category(String categoryName)
    {
        m_id = UUID.randomUUID();
        m_categoryName = categoryName;
    }

    public UUID getId() {return m_id;};
    public String getCategoryName() {return m_categoryName;};

    public void setCategoryName(String value){m_categoryName = value;};
    
}
