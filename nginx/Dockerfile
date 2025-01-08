FROM yeriimii/upup-radio-front:react AS builder

FROM nginx:1.27.2

COPY --from=builder /app/build /usr/share/nginx/html

COPY ./upup-radio.conf /etc/nginx/conf.d/default.conf

EXPOSE 80 443

CMD [ "nginx", "-g", "daemon off;" ]
