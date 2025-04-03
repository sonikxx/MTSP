
CREATE TABLE mtsp_organizations (
                                    id SERIAL PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL UNIQUE,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE mtsp_users (
                            id SERIAL PRIMARY KEY,
                            organization_id INT NOT NULL,
                            first_name VARCHAR(100) NOT NULL,
                            last_name VARCHAR(100) NOT NULL,
                            email VARCHAR(255) UNIQUE NOT NULL,
                            password_hash TEXT NOT NULL,
                            is_admin BOOLEAN DEFAULT FALSE,
                            last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                            FOREIGN KEY (organization_id) REFERENCES mtsp_organizations(id) ON DELETE CASCADE
);


CREATE TYPE mtsp_request_status AS ENUM ('QUEUED', 'INTERMEDIATE', 'SOLVED', 'FAILED');

CREATE TABLE mtsp_solutions (
                                id SERIAL PRIMARY KEY,
                                request_id VARCHAR(40) NOT NULL,
                                user_id INT NOT NULL,
                                status mtsp_request_status NOT NULL DEFAULT 'QUEUED',
                                total_cost DOUBLE PRECISION,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                completed_at TIMESTAMP,

                                FOREIGN KEY (user_id) REFERENCES mtsp_users(id) ON DELETE SET NULL
);

CREATE TABLE mtsp_routes (
                             id SERIAL PRIMARY KEY,
                             solution_id INT NOT NULL,
                             salesman_index INT NOT NULL CHECK (salesman_index >= 0),
                             points TEXT NOT NULL,

                             FOREIGN KEY (solution_id) REFERENCES mtsp_solutions(id) ON DELETE CASCADE
);
