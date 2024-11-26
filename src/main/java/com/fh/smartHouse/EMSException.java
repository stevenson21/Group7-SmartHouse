package com.fh.smartHouse;


/**
 * Base exception class for the Smart House project.
 */
public class EMSException extends Exception {
    public EMSException(String message) {
        super(message);
    }

    public EMSException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Specific Exceptions for the Smart House project.
 */

// Insufficient battery charge exception
class InsufficientChargeException extends EMSException {
    public InsufficientChargeException(String message) {
        super(message);
    }

    public InsufficientChargeException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Smart object operation failure
class SmartObjectException extends EMSException {
    public SmartObjectException(String message) {
        super(message);
    }

    public SmartObjectException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Energy source operation failure
class EnergySourceException extends EMSException {
    public EnergySourceException(String message) {
        super(message);
    }

    public EnergySourceException(String message, Throwable cause) {
        super(message, cause);
    }
}

// File-related exceptions
class EMSFileNotFoundException extends EMSException {
    public EMSFileNotFoundException(String message) {
        super(message);
    }

    public EMSFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

class EMSFileReadException extends EMSException {
    public EMSFileReadException(String message) {
        super(message);
    }

    public EMSFileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Regex or pattern-related exceptions
class EMSInvalidRegexException extends EMSException {
    public EMSInvalidRegexException(String message) {
        super(message);
    }

    public EMSInvalidRegexException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Resource-related exceptions
class EMSResourceException extends EMSException {
    public EMSResourceException(String message) {
        super(message);
    }

    public EMSResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Usage-related exceptions (e.g., when usage exceeds limits)
class EMSUsageException extends EMSException {
    public EMSUsageException(String message) {
        super(message);
    }

    public EMSUsageException(String message, Throwable cause) {
        super(message, cause);
    }
}

// General configuration or initialization exceptions
class EMSConfigurationException extends EMSException {
    public EMSConfigurationException(String message) {
        super(message);
    }

    public EMSConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Additional Custom Exceptions for Specific Scenarios.
 */

// Unauthorized access or operation exceptions
class EMSUnauthorizedAccessException extends EMSException {
    public EMSUnauthorizedAccessException(String message) {
        super(message);
    }

    public EMSUnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Overload or capacity breach exception
class EMSCapacityOverloadException extends EMSException {
    public EMSCapacityOverloadException(String message) {
        super(message);
    }

    public EMSCapacityOverloadException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Sensor failure or invalid readings exception
class EMSSensorFailureException extends EMSException {
    public EMSSensorFailureException(String message) {
        super(message);
    }

    public EMSSensorFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
