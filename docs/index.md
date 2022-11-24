# Sample Application Architecture

__Table of Contents__

* [Repository structure](#repository-structure)
* [Application Components](#application-components)

## Repository Structure

* [nats-transport-starter](../nats-transport-starter): Provides a set of classes and configurations for sending messages
  to NATS and creating NATS listeners.
* [user-service](../user-service): Microservice for working with users of the system. Provides the ability to create,
  delete, manage users.

## Application Components

![Component diagram](./assets/diagrams/component.png)