export const TASK_STATUSES = [
  "TODO",
  "IN_PROGRESS",
  "ON_REVIEW",
  "REOPENED",
  "READY_TO_MERGE",
  "MERGED_TO_MASTER",
  "DEV_DEPLOYED",
  "DEV_VERIFIED",
  "STAGE_DEVELOPED",
  "STAGE_VERIFIED",
  "PROD_DEPLOYED",
  "PROD_VERIFIED",
  "DONE",
];

export const PROJECT_STATUSES = ["TODO", "IN_PROGRESS", "DONE", "CANCELED"];
export const PRIORITIES = ["LOW", "MEDIUM", "HIGH"];
export const CATEGORIES = ["BUG", "FEATURE"];
export const ROLES = ["ADMIN", "MANAGER", "EMPLOYEE", "CLIENT"];

export function label(value) {
  if (!value) return "Unassigned";
  return value
    .toString()
    .toLowerCase()
    .replaceAll("_", " ")
    .replace(/\b\w/g, (letter) => letter.toUpperCase());
}

export function formatDate(value) {
  if (!value) return "No date";
  return new Intl.DateTimeFormat("en", {
    month: "short",
    day: "numeric",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(value));
}

export function toDateTimeLocal(value) {
  if (!value) return "";
  const date = new Date(value);
  date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
  return date.toISOString().slice(0, 16);
}

export function fromDateTimeLocal(value) {
  return value ? new Date(value).toISOString().slice(0, 19) : null;
}

export function initials(name = "User") {
  return name
    .split(" ")
    .slice(0, 2)
    .map((part) => part[0])
    .join("")
    .toUpperCase();
}
