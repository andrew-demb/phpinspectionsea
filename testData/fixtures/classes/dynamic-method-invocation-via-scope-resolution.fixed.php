<?php

class ParentCasesHolder
{
    public function method() {}
}

class CasesHolder extends ParentCasesHolder
{
    public function method() {}

    public static function staticTrigger()
    {
        static::method();
    }

    public function dynamicTrigger()
    {
        $this->method();
        $this->method();
        $this->method();
    }
}

class ChildCasesHolder extends CasesHolder
{
    public function method()
    {
        return [
            CasesHolder::method(),
            ParentCasesHolder::method(),
        ];
    }
}

$object = new CasesHolder();
$object->method();