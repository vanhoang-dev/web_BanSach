interface ValidateFileOptions {
  maxSizeInMb?: number;
  allowedTypes?: string[];
}

export function validateFile(file: File, options: ValidateFileOptions = {}) {
  const { maxSizeInMb = 5, allowedTypes = [] } = options;
  const maxSizeInBytes = maxSizeInMb * 1024 * 1024;

  if (file.size > maxSizeInBytes) {
    return `File must be smaller than ${maxSizeInMb}MB`;
  }

  if (allowedTypes.length > 0 && !allowedTypes.includes(file.type)) {
    return 'File type is not supported';
  }

  return null;
}
