
CREATE TABLE mtsp_users (
                            id SERIAL PRIMARY KEY,
                            first_name VARCHAR(100) NOT NULL,
                            last_name VARCHAR(100) NOT NULL,
                            email VARCHAR(255) UNIQUE NOT NULL,
                            password_hash TEXT NOT NULL,
                            last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE mtsp_solutions (
                                id SERIAL PRIMARY KEY,
                                request_id VARCHAR(40) NOT NULL,
                                user_id INT NOT NULL,
                                status VARCHAR(20) NOT NULL DEFAULT 'QUEUED', -- 'QUEUED', 'INTERMEDIATE', 'SOLVED', 'FAILED'
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

CREATE TABLE mtsp_maps
(
    id        SERIAL PRIMARY KEY,
    user_id   INT          NOT NULL,
    name      VARCHAR(255) NOT NULL,
    is_public BOOLEAN DEFAULT FALSE,
    points    TEXT         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES mtsp_users (id) ON DELETE CASCADE
);

CREATE TABLE mtsp_requests (
                               id VARCHAR(40) PRIMARY KEY,
                               user_id          INT         NOT NULL,
                               status           VARCHAR(20) NOT NULL DEFAULT 'QUEUED' CHECK (status IN ('QUEUED', 'SOLVED', 'CANCELED', 'FAILED')),
                               created_at       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,

                               map_id           INT         NOT NULL,
                               salesman_number  INT         NOT NULL,
                               algorithm        VARCHAR(50) NOT NULL,
                               algorithm_params TEXT,
                               FOREIGN KEY (user_id) REFERENCES mtsp_users(id) ON DELETE CASCADE,
                               FOREIGN KEY (map_id) REFERENCES mtsp_maps(id) ON DELETE CASCADE
);

CREATE TABLE mtsp_edges
(
    id SERIAL PRIMARY KEY,
    map_id INT NOT NULL,
    from_node INT NOT NULL,
    to_node   INT NOT NULL,
    distance  DOUBLE PRECISION NOT NULL,

    FOREIGN KEY (map_id) REFERENCES mtsp_maps (id) ON DELETE CASCADE
);

CREATE TABLE mtsp_logs
(
    request_id VARCHAR(40) NOT NULL,
    user_id   INT         NOT NULL,
    level     VARCHAR(10) NOT NULL,
    message   TEXT        NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (request_id) REFERENCES mtsp_requests (id) ON DELETE CASCADE
);
