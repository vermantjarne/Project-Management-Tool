# Project-Management-Tool
The goal of the project is creating a solution for managing employees, projects and tasks within an organization. The system is structured as a microservices architecture to ensure scalability and maintainability for future adjustments.

The application contains 3 microservices: `employee-service`, `project-service` and `task-service`. Each of these services manages its own database. The services communicate with each other via HTTP REST endpoints, coordinated through the API gateway `api-gateway`.


## Infrastructure
### Containerization
All services are containerized using Docker. Each service has its own `Dockerfile`.

### Docker Compose
The containers are orchestrated for local development and testing through the docker compose file `docker-compose.yml`.

### CI/CD
GitHub Actions handles the build, test and deployment process for each service through `build-containers.yml`


## Employee Service
The `employee-service` allows the user to manage employees within the system.

- Port: `8080:8080`
- Docker image: `jarnevermant/employee-service`

### Database
- Technology: MongoDB
- Service: `mongo-employee`
- Database name: `mongodb_employee_data`
- Port: `27017:27017`

### API Endpoints
| Method | Endpoint |
| ------ | -------- |
| `POST` | `/api/employee` |
| `GET` | `/api/employee/all` |
| `GET` | `/api/employee/{employeeIdentifier}` |
| `UPDATE` | `/api/employee/{employeeIdentifier}` |
| `DELETE` | `/api/employee/{employeeIdentifier}` |

### Error handling
This service includes custom error handling for its endpoints.
| Error | Description |
| ----- | ----------- |
| `EmployeeNotFound` | Thrown when a call is made to this service requiring an employee that does not exist |


## Project Service
The `project-service` allows the user to manage projects within the system. This service makes use of a MySQL database.

- Port: `8081:8081`
- Docker image: `jarnevermant/project-service`

### Database
- Technology: MySQL
- Service: `mysql-project`
- Database name: `mysql_project_data`
- Port: `3306:3306`

### API Endpoints
| Method | Endpoint |
| ------ | -------- |
| `POST` | `/api/project` |
| `GET` | `/api/project/all` |
| `GET` | `/api/project/active` |
| `GET` | `/api/project/{projectIdentifier}` |
| `UPDATE` | `/api/project/{projectIdentifier}` |
| `DELETE` | `/api/project/{projectIdentifier}` |

### Error handling
This service includes custom error handling for its endpoints.
| Error | Description |
| ----- | ----------- |
| `EmployeeNotFound` | Thrown when a call is made to this service requiring an employee that does not exist |
| `InvalidEmployeeRole` | Thrown when a call is made to this service requiring an employee with the incorrect role |
| `ProjectNotFound` | Thrown when a call is made to this service requiring a project that does not exist |


## Task Service
The `task-service` allows the user to manage tasks within the system.

- Port: `8082:8082`
- Docker image: `jarnevermant/task-service`

### Database
- Technology: MySQL
- Service: `mysql-task`
- Database name: `mysql_task_data`
- Port: `3307:3306`

### API Endpoints
| Method | Endpoint |
| ------ | -------- |
| `POST` | `/api/task` |
| `GET` | `/api/task/all` |
| `GET` | `/api/task/active` |
| `GET` | `/api/task/{taskIdentifier}` |
| `GET` | `/api/task/project/{projectIdentifier}` |
| `UPDATE` | `/api/task/{taskIdentifier}` |
| `DELETE` | `/api/task/{taskIdentifier}` |

### Error handling
This service includes custom error handling for its endpoints.
| Error | Description |
| ----- | ----------- |
| `EmployeeNotFound` | Thrown when a call is made to this service requiring an employee that does not exist |
| `ProjectNotFound` | Thrown when a call is made to this service requiring a project that does not exist |
| `TaskNotFound` | Thrown when a call is made to this service requiring a task that does not exist |


## API Gateway
The `task-service` allows the user to call other services. It routes requests to the correct services. It implements security features, making use of OAuth2 with JWT. This service makes use of Spring Cloud Gateway.

Every endpoint, aside from the `GET` for `/employee`, requires an authenticated connection.

- Port: `8083:8083`
- Docker image: `jarnevermant/api-gateway`

### API Endpoints
| Method | Endpoint | Description | Auth |
| ------ | -------- | ----------- | ---- |
| `POST` | `/employee` | Adds a new employee | x |
| `GET` | `/employee` | Retrieves all employees |
| `GET` | `/employee/{employeeIdentifier}` | Retrieves an employee by id | x |
| `UPDATE` | `/employee/{employeeIdentifier}` | Updates an employee | x |
| `DELETE` | `/employee/{employeeIdentifier}` | Removes an employee | x |
| `POST` | `/project` | Creates a new project | x |
| `GET` | `/project` | Retrieves all projects | x |
| `GET` | `/project/active` | Retrieves all projects currently active | x |
| `GET` | `/project/{projectIdentifier}` | Retrieves a project by id | x |
| `UPDATE` | `/project/{projectIdentifier}` | Updates a project | x |
| `DELETE` | `/project/{projectIdentifier}` | Deletes a project | x |
| `POST` | `/task` | Creates a task | x |
| `GET` | `/task` | Retrieves all tasks | x |
| `GET` | `/task/active` | Retrieves all tasks with status "In Progress" | x |
| `GET` | `/task/{taskIdentifier}` | Retrieves a task by id | x |
| `GET` | `/task/project/{projectIdentifier}` | Retrieves all tasks of a project | x |
| `UPDATE` | `/task/{taskIdentifier}` | Updates a task | x |
| `DELETE` | `/task/{taskIdentifier}` | Deletes a task | x |


## Postman Screenshots
Screenshots of the usage of this endpoint in Postman can be found [here](/api-gateway.md).