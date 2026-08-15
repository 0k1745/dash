FROM node:22-alpine AS build
WORKDIR /workspace

COPY pnpm-workspace.yaml package.json pnpm-lock.yaml ./
COPY shell/package.json shell/package.json
COPY features/task-manager/package.json features/task-manager/package.json

RUN corepack enable && corepack prepare pnpm@9 --activate
RUN pnpm install --frozen-lockfile

COPY tsconfig.base.json ./
COPY shell shell
COPY features/task-manager features/task-manager

ARG VITE_TASK_MANAGER_API_URL=http://localhost:8080
ENV VITE_TASK_MANAGER_API_URL=${VITE_TASK_MANAGER_API_URL}
RUN pnpm --filter shell build

FROM nginx:1.27-alpine AS runtime
COPY --from=build /workspace/shell/dist /usr/share/nginx/html
EXPOSE 80
