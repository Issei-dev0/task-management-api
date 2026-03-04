CREATE TABLE task (
                      id BIGSERIAL PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      description TEXT,
                      status VARCHAR(50),
                      owner_username VARCHAR(255),
                      created_at TIMESTAMP,
                      updated_at TIMESTAMP,
                      due_date TIMESTAMP
);